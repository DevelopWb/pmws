package com.juntai.wisdom.basecomponent.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juntai.wisdom.basecomponent.mvp.IPresenter;
import com.juntai.wisdom.basecomponent.mvp.IView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;

public abstract class BaseMvpFragment<P extends IPresenter> extends BaseLazyFragment implements IView {

    protected P mPresenter;
    protected abstract P createPresenter();
    ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        progressDialog = new ProgressDialog(mContext);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        this.mPresenter = null;
    }

    @Override
    public void showLoading() {
        getBaseActivity().showLoadingDialog(getContext());
    }

    @Override
    public void hideLoading() {
        getBaseActivity().stopLoadingDialog();
    }
    @Override
    public void showMsg(String message) {

    }

    @Override
    public void showDefaultMsg(String msg) {

    }

    @Override
    public void showErrorMsg(String errorMsg) {

    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }
    /**
     * 查找viewid
     * @param viewId
     * @param <V>
     * @return
     */
    public <V extends View> V getView(int viewId){
        return mRootView.findViewById(viewId);
    }

}
