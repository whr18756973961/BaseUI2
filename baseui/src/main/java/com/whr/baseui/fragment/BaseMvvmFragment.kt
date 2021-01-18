package com.whr.baseui.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.whr.baseui.R
import com.whr.baseui.activity.BaseActivity
import com.whr.baseui.activity.BaseMvvmActivity
import com.whr.baseui.bean.ViewStatusEnum
import com.whr.baseui.helper.UiCoreHelper
import com.whr.baseui.mvp.BaseMvpView
import com.whr.baseui.mvvm.BaseViewModel
import com.whr.baseui.swipeback.SwipeBackFragment
import com.whr.baseui.utils.StatusBarUtils
import com.whr.baseui.utils.TUtils
import com.whr.baseui.widget.StatusView
import com.whr.baseui.widget.WaitProgressDialog

abstract class BaseMvvmFragment<V : ViewDataBinding, VM : BaseViewModel> : SwipeBackFragment(),
    BaseMvpView,
    LifecycleObserver {
    lateinit var mBinding: V
    lateinit var mViewModel: VM
    private var providerVMClass: Class<VM>? = null

    lateinit var mActivity: BaseMvvmActivity<*, *>
    lateinit var mFragment: BaseMvvmFragment<*, *>
    var mRootView: ViewGroup? = null
    var mStatusView: StatusView? = null
    var mRootViewParent: LinearLayout?=null

    /**
     * 对话框
     */
    private var mWaitDialog: WaitProgressDialog? = null

    /**
     * 頂部導航欄
     */
    lateinit var mHeadView: View

    lateinit var mFakeStatusBar: View

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

    /**
     * 当前fragment 是否被装如viewpager中
     */
    var isAttachViewPager: Boolean = false

    /**
     * 该页面，是否已经准备完毕
     * */
    private var isPrepared: Boolean = false

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


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity is BaseMvvmActivity<*, *>)
            mActivity = activity
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragment = this
        initMVVM()
        initViewDataBinding(inflater, container)
        if (mRootView == null) {
            mRootView = mBinding.root as ViewGroup
            mRootViewParent= layoutInflater.inflate(R.layout.fragment_base, null) as LinearLayout?
            mHeadView = layoutInflater.inflate(UiCoreHelper.getProxyA().headerIdRes(), null)
            initHeadView()
            // 虚拟導航状态栏
            mFakeStatusBar =
                LayoutInflater.from(mActivity)
                    .inflate(R.layout.layout_fake_statusbar, mRootView, false)
            val layoutParams = mFakeStatusBar.layoutParams
            layoutParams.height = StatusBarUtils.getStatusBarHeight(mActivity)
            mFakeStatusBar.layoutParams = layoutParams
            mFakeStatusBar.setBackgroundColor(
                resources.getColor(
                    UiCoreHelper.getProxyA().colorPrimaryDark()
                )
            )
            mFakeStatusBar.visibility = View.GONE
            mRootViewParent?.addView(mFakeStatusBar, 0)
            mRootViewParent?.addView(mHeadView, 1)
        }
        startObserve()
        vStatusObserve()
        mRootView?.parent?.let {
            (it as ViewGroup).removeView(mRootView)
        }
        mRootViewParent?.addView(mRootView)
        return attachToSwipeBack(mRootView!!)
    }

    abstract fun initVariableId(): Int

    /**
     * 注入绑定
     */
    private fun initViewDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mBinding.setVariable(initVariableId(), mViewModel)
        mBinding.executePendingBindings()
        //liveData绑定activity，fragment生命周期
        mBinding.lifecycleOwner = this
    }

    fun initHeadView() {
        mIvBack = mHeadView.findViewById(UiCoreHelper.getProxyA().headerBackId())
        mTvTitle = mHeadView.findViewById(UiCoreHelper.getProxyA().headerTitleId())
        mTvRight = mHeadView.findViewById(UiCoreHelper.getProxyA().headerRightId())
        mIvRight = mHeadView.findViewById(UiCoreHelper.getProxyA().headerRightIconId())
        mIvBack.setOnClickListener { mActivity.onActivityFinish() }
        mHeadView.visibility = View.GONE
    }

    private fun initMVVM() {
        providerVMClass = TUtils.getT<VM>(this, 1).javaClass
        providerVMClass?.let {
            mViewModel = ViewModelProviders.of(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }

    /**
     * 错误界面的点击事件
     */
    open fun onErrorReplyClick() {

    }


    open fun getColor(colorID: Int): Int {
        return ContextCompat.getColor(context!!,colorID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBundle(arguments)
        initView(view)
    }

    /**
     * 参数校验
     *
     * @param bundle
     */
    open fun handleBundle(bundle: Bundle?) {}

    /**
     * 初始化控件
     *
     * @param view
     */
    abstract fun initView(view: View)


    /**
     * header头部的返回键监听时间
     */
    fun onHeaderBackPressed() {
        mActivity.onActivityBackPressed()
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        var enable = enable
        val count = mActivity.supportFragmentManager.backStackEntryCount
        if (mActivity.layoutId == 0) {
            enable = count > 1
        }
        super.setSwipeBackEnable(enable)
    }

    /**
     * 显示对话框
     */
    override fun showWaitDialog() {
        mActivity.showWaitDialog()
    }

    override fun showWaitDialog(message: String) {
        mActivity.showWaitDialog(message)
    }

    override fun showWaitDialog(message: String, cancelable: Boolean) {
        mActivity.showWaitDialog(message, cancelable)
    }

    override fun isWaitDialogShow(): Boolean {
        return mActivity.isWaitDialogShow()
    }

    override fun getWaitDialog(): Dialog {
        return mActivity.getWaitDialog()
    }

    override fun getmActivity(): BaseActivity? {
        return null
    }

    override fun getmFragment(): BaseFragment? {
        return null
    }

    override fun hideWaitDialog() {
        mActivity.hideWaitDialog()
    }

    override fun showToast(msg: String?) {
        mActivity.showToast(msg)
    }

    override fun showToast(strId: Int) {
        mActivity.showToast(strId)
    }

    override fun showToast(strId1: Int, strin2: Int) {
        mActivity.showToast(strId1, strin2)
    }

    override fun showToast(strId1: Int, strin2: String?) {
        mActivity.showToast(strId1, strin2)
    }


    private fun initStatusView() {
        if (mStatusView == null)
            mStatusView = StatusView(this)
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

    override fun hideStatusView() {
        if (mStatusView != null)
            mStatusView!!.hideStatusView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isPrepared = true
        UiCoreHelper.getProxyA().onFragmentActivityCreated(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiCoreHelper.getProxyA().onFragmentCreate(this)
    }

    override fun onStart() {
        super.onStart()
        UiCoreHelper.getProxyA().onFragmentStart(this)
    }

    override fun onStop() {
        super.onStop()
        UiCoreHelper.getProxyA().onFragmentStop(this)
    }

    override fun onPause() {
        super.onPause()
        UiCoreHelper.getProxyA().onFragmentPause(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        UiCoreHelper.getProxyA().onFragmentDestroyView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        UiCoreHelper.getProxyA().onFragmentDestroy(this)
    }

    override fun onDetach() {
        super.onDetach()
        UiCoreHelper.getProxyA().onFragmentDetach(this)
    }

    /**
     * 此方法会在fragment ，显示是调用，相当于fragment的onresume方法，可以做返回刷新操作
     */
    open fun onFragmentVisible() {

    }

    /**
     * 此方法判断fragment是否显示，在viewpager时会失效
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            Log.e("999999991",javaClass.name)
            onFragmentVisible()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("999999992",javaClass.name)
        if (isPrepared && isAttachViewPager && isVisibleToUser && !isHidden) {
            Log.e("999999992",javaClass.name)
            onFragmentVisible()
        }
    }

    override fun onResume() {
        super.onResume()
        UiCoreHelper.getProxyA().onFragmentResume(this)
        Log.e("999999993",javaClass.name  +!isHidden+"  "+userVisibleHint+"  "+(isPrepared || !isAttachViewPager))
        if (!isHidden && userVisibleHint && (isPrepared || !isAttachViewPager)) {
            onFragmentVisible()

        }
    }

    open fun startObserve() {

    }

    private fun vStatusObserve() {
        mViewModel.apply {
            vStatus.observe(this@BaseMvvmFragment,
                Observer {
                    when (it["status"]) {
                        ViewStatusEnum.SHOWWAITDIALOG -> {
                            if (it.containsKey("msg")) {
                                if (it.containsKey("cancelable"))
                                    mFragment.showWaitDialog(
                                        it["msg"].toString(),
                                        it["cancelable"] as Boolean
                                    )
                                else
                                    mFragment.showWaitDialog(it["msg"].toString())
                            } else {
                                mFragment.showWaitDialog()
                            }
                        }
                        ViewStatusEnum.HIDEWAITDIALOG -> {
                            mFragment.hideWaitDialog()
                        }
                        ViewStatusEnum.SHOWSTATUSEMPTYVIEW -> {
                            if (it.containsKey("msg")) {
                                mFragment.showStatusEmptyView(it["msg"].toString())
                            }
                        }
                        ViewStatusEnum.SHOWSTATUSLOADINGVIEW -> {
                            if (it.containsKey("msg")) {
                                if (it.containsKey("isHasMinTime"))
                                    mFragment.showStatusLoadingView(
                                        it["msg"].toString(),
                                        it["isHasMinTime"] as Boolean
                                    )
                                else
                                    mFragment.showStatusLoadingView(it["msg"].toString())
                            }
                        }
                        ViewStatusEnum.SHOWSTATUSERRORVIEW -> {
                            if (it.containsKey("msg")) {
                                mFragment.showStatusErrorView(it["msg"].toString())
                            }
                        }
                        ViewStatusEnum.HIDESTATUSVIEW -> {
                            mFragment.hideStatusView()
                        }
                        ViewStatusEnum.SHOWTOAST -> {
                            mFragment.showToast(it["msg"].toString())
                        }
                    }
                })
        }
    }
}