package com.whr.baseui.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.whr.baseui.R
import com.whr.baseui.bean.ViewStatusEnum
import com.whr.baseui.fragment.BaseFragment
import com.whr.baseui.helper.UiCoreHelper
import com.whr.baseui.mvp.BaseMvpView
import com.whr.baseui.mvvm.BaseViewModel
import com.whr.baseui.swipeback.SwipeBackActivity
import com.whr.baseui.utils.*
import com.whr.baseui.widget.StatusView
import com.whr.baseui.widget.WaitProgressDialog


abstract class BaseMvvmActivity<V : ViewDataBinding, VM : BaseViewModel> : SwipeBackActivity(),
    BaseMvpView,
    View.OnClickListener,
    LifecycleObserver {
    lateinit var mBinding: V
    lateinit var mViewModel: VM
    private var providerVMClass: Class<VM>? = null
    private var receiver: MyNetBroadCastReciver? = null
    lateinit var mActivity: BaseMvvmActivity<*, *>
    lateinit var mRootView: ViewGroup

    private var mRootViewParent: LinearLayout ?=null

    var mContentView: FrameLayout?=null

    var mStatusView: StatusView? = null

    /**
     * 对话框
     */
    private var mWaitDialog: WaitProgressDialog? = null

    lateinit var mFakeStatusBar: View

    /**
     * 頂部導航欄
     */
    lateinit var mHeadView: View

    /**
     * 返回按鈕
     */
    lateinit var mIvBack: ImageView

    /**
     * 標題
     */
    lateinit var mTvTitle: TextView

    /**
     * 右側文字按鈕
     */
    lateinit var mTvRight: TextView

    /**
     * 右侧图片按钮
     */
    lateinit var mIvRight: ImageView

    /**
     * 底部按钮
     */
    lateinit var mBtmLine: View

    /**
     * 返回页面布局
     *
     * @return
     */
    @get:LayoutRes
    abstract val layoutId: Int
    lateinit var viewStub: ViewStub

    /**
     * 是否支持双击，默认为不支持
     */
    private val mDoubleClickEnable = false

    /**
     * 上一次点击的时间戳
     */
    private var mLastClickTime: Long = 0

    /**
     * 被判断为重复点击的时间间隔
     */
    private val MIN_CLICK_DELAY_TIME: Long = 200

    /**
     * 检测双击
     */
    val isDoubleClick: Boolean
        get() {
            if (mDoubleClickEnable) return false
            val time = System.currentTimeMillis()
            if (time - mLastClickTime > MIN_CLICK_DELAY_TIME) {
                mLastClickTime = time
                return false
            } else {
                return true
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppManager.getAppManager().addActivity(this)
        UiCoreHelper.getProxyA().onActivityCreate(this)
        setStatusBar()
        initMVVM()
        initViewDataBinding()
        startObserve()
        vStatusObserve()
    }


    abstract fun initVariableId(): Int

    /**
     * 注入绑定
     */
    private fun initViewDataBinding() {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        mBinding = DataBindingUtil.setContentView(this, layoutId)
        mBinding.setVariable(initVariableId(), mViewModel)
        mBinding.executePendingBindings()

        //liveData绑定activity，fragment生命周期
        mBinding.lifecycleOwner = this
        mRootView = mBinding.root as ViewGroup

        mRootViewParent= layoutInflater.inflate(R.layout.activity_base, null) as LinearLayout?
        mContentView=mRootViewParent?.findViewById(R.id.fl_container)
        mHeadView = layoutInflater.inflate(UiCoreHelper.getProxyA().headerIdRes(), null)
        initHeadView()
        // 虚拟導航状态栏
        mFakeStatusBar =
            LayoutInflater.from(this).inflate(R.layout.layout_fake_statusbar, mRootView, false)
        val layoutParams = mFakeStatusBar.layoutParams
        layoutParams.height = StatusBarUtils.getStatusBarHeight(this)
        mFakeStatusBar.layoutParams = layoutParams
        mFakeStatusBar.setBackgroundColor(
            resources.getColor(
                UiCoreHelper.getProxyA().colorPrimaryDark()
            )
        )
        mRootViewParent?.addView(mFakeStatusBar, 0)
        mRootViewParent?.addView(mHeadView, 1)
        mRootView.parent?.let {
            (mRootView.parent as ViewGroup).removeView(mRootView)
        }
        mContentView ?.addView(mRootView)
        setContentView(mRootViewParent)
        if (null != intent) handleIntent(intent)
        initView(mRootViewParent!!)
    }

    fun initHeadView() {
        mIvBack = mHeadView.findViewById(UiCoreHelper.getProxyA().headerBackId())
        mTvTitle = mHeadView.findViewById(UiCoreHelper.getProxyA().headerTitleId())
        mTvRight = mHeadView.findViewById(UiCoreHelper.getProxyA().headerRightId())
        mIvRight = mHeadView.findViewById(UiCoreHelper.getProxyA().headerRightIconId())
        mIvBack.setOnClickListener { onActivityFinish() }
        mBtmLine = mHeadView.findViewById(UiCoreHelper.getProxyA().headerBtmLineId())
    }

    private fun initMVVM() {
        providerVMClass = TUtils.getT<VM>(this, 1).javaClass
        providerVMClass?.let {
            mViewModel = ViewModelProviders.of(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }

    /**
     * 当activity结束时候调用
     */
    fun onActivityFinish() {
        finish()
    }

    /**
     * 初始化状态页面
     */
    private fun initStatusView() {
        if (mStatusView == null)
            mStatusView = StatusView(this)
    }

    /**
     * 获取Intent
     *
     * @param intent
     */
    open fun handleIntent(intent: Intent) {}

    /**
     * 初始化控件
     *
     * @param rootView
     */
    protected abstract fun initView(rootView: View)

    /**
     * 默认黑色状态栏主题，就是状态栏字体黑色
     */
    fun setStatusBarStyle(isDark: Boolean) {
        StatusBarDarkUtil.setStatusBarMode(this, isDark)
    }

    /**
     * 设置状态栏颜色
     */
    fun setStatusBar() {
        StatusBarUtils.setTranslucent(this, 0)
        setStatusBarStyle(true)
    }

    override fun onClick(v: View) {

    }

    /**
     * 处理返回操作
     *
     * @return
     */
    fun onActivityBackPressed(): Boolean {
        // 如果getLayoutId()==0说明没有加载Activity自己的布局，只是单独加载了Fragment
        val count = supportFragmentManager.backStackEntryCount

        if (layoutId == 0 && count == 1) {
            onActivityFinish()
            return true
        }

        if (layoutId != 0 && count == 0) {
            onActivityFinish()
            return true
        }
        FragmentUtils.popFragment(supportFragmentManager)
        return false
    }


    override fun showWaitDialog() {
        showWaitDialog("Loading")
    }

    override fun showWaitDialog(message: String) {
        if (TextUtils.isEmpty(message))
            showWaitDialog()
        else
            showWaitDialog(message, true)
    }

    override fun showWaitDialog(message: String, cancelable: Boolean) {
        if (mWaitDialog == null) {
            mWaitDialog = WaitProgressDialog(this)
        }
        mWaitDialog!!.show(message, cancelable)
    }


    override fun getmFragment(): BaseFragment? {
        return null
    }

    override fun getmActivity(): BaseActivity? {
        return null
    }

    override fun isWaitDialogShow(): Boolean {
        return mActivity.isWaitDialogShow()
    }

    override fun getWaitDialog(): Dialog {
        return mActivity.getWaitDialog()
    }

    override fun hideWaitDialog() {
        if (mWaitDialog != null && mWaitDialog!!.isShowing) {
            mWaitDialog!!.dismiss()
        }
    }

    override fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    override fun showToast(strId: Int) {
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show()
    }

    override fun showToast(strId1: Int, str: Int) {
        Toast.makeText(this, getString(strId1) + getString(str), Toast.LENGTH_SHORT).show()
    }

    override fun showToast(strId1: Int, strin2: String?) {
        Toast.makeText(this, getString(strId1) + strin2, Toast.LENGTH_SHORT).show()
    }

    override fun showStatusEmptyView(emptyMessage: String) {
        initStatusView()
        mStatusView!!.showEmptyView(emptyMessage)
    }

    override fun showStatusErrorView(emptyMessage: String) {
        initStatusView()
        mStatusView!!.showErrorView(emptyMessage)
    }

    override fun showStatusLoadingView(loadingMessage: String) {
        initStatusView()
        mStatusView!!.showLoadingView(loadingMessage)
    }

    override fun showStatusLoadingView(loadingMessage: String, isHasMinTime: Boolean) {

    }

    fun onErrorReplyClick() {

    }

    override fun hideStatusView() {
        if (mStatusView != null)
            mStatusView!!.hideStatusView()
    }


    override fun onStart() {
        super.onStart()
        UiCoreHelper.getProxyA().onActivityStart(this)
    }

    override fun onRestart() {
        super.onRestart()
        UiCoreHelper.getProxyA().onActivityRestart(this)
    }

    override fun onResume() {
        super.onResume()
        UiCoreHelper.getProxyA().onActivityResume(this)
    }

    override fun onPause() {
        super.onPause()
        UiCoreHelper.getProxyA().onActivityPause(this)
    }

    override fun onStop() {
        super.onStop()
        UiCoreHelper.getProxyA().onActivityStop(this)
    }


    /**
     * 重写此方法是为了Fragment的onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UiCoreHelper.getProxyA().onActivityResult(this, requestCode, resultCode, data)
    }


    private fun vStatusObserve() {
        mViewModel.apply {
            vStatus.observe(this@BaseMvvmActivity,
                Observer {
                    when (it["status"]) {
                        ViewStatusEnum.SHOWWAITDIALOG -> {
                            if (it.containsKey("msg")) {
                                if (it.containsKey("cancelable"))
                                    showWaitDialog(
                                        it["msg"].toString(),
                                        it["cancelable"] as Boolean
                                    )
                                else
                                    mActivity.showWaitDialog(it["msg"].toString())
                            } else {
                                mActivity.showWaitDialog()
                            }
                        }
                        ViewStatusEnum.HIDEWAITDIALOG -> {
                            mActivity.hideWaitDialog()
                        }
                        ViewStatusEnum.SHOWSTATUSEMPTYVIEW -> {
                            if (it.containsKey("msg")) {
                                mActivity.showStatusEmptyView(it["msg"].toString())
                            }
                        }
                        ViewStatusEnum.SHOWSTATUSLOADINGVIEW -> {
                            if (it.containsKey("msg")) {
                                if (it.containsKey("isHasMinTime"))
                                    mActivity.showStatusLoadingView(
                                        it["msg"].toString(),
                                        it["isHasMinTime"] as Boolean
                                    )
                                else
                                    mActivity.showStatusLoadingView(it["msg"].toString())
                            }
                        }
                        ViewStatusEnum.SHOWSTATUSERRORVIEW -> {
                            if (it.containsKey("msg")) {
                                mActivity.showStatusErrorView(it["msg"].toString())
                            }
                        }
                        ViewStatusEnum.HIDESTATUSVIEW -> {
                            mActivity.hideStatusView()
                        }
                        ViewStatusEnum.SHOWTOAST -> {
                            mActivity.showToast(it["msg"].toString())
                        }
                    }
                })
        }
    }

    open fun startObserve() {

    }

    /**
     * 监听网络状态变化
     */
    internal inner class MyNetBroadCastReciver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //如果是在开启wifi连接和有网络状态下
            if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val info =
                    intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
                if (NetworkInfo.State.CONNECTED == info?.state) {
                    //连接状态 处理自己的业务逻辑
                } else {
                }
            }
        }
    }

    override fun onDestroy() {
        mViewModel.let {
            lifecycle.removeObserver(it)
        }
        hideSystemSoftInput()
        super.onDestroy()
        AppManager.getAppManager().finishActivity(this)
        UiCoreHelper.getProxyA().onActivityDestory(this)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            if (isDoubleClick) {
                return true
            }
        }
        //点击软键盘外部，软键盘消失
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v!!.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false
    }

   private fun hideSystemSoftInput() {
        val view = window.peekDecorView()
        if (view != null && view.windowToken != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(
                token,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}