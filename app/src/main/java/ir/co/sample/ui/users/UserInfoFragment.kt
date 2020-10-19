package ir.co.sample.ui.users

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import ir.co.common.base.FragmentParent
import ir.co.common.utils.IntentManager
import ir.co.repository.di.DataBaseModule
import ir.co.repository.di.getRepositoryComponent
import ir.co.sample.R
import ir.co.sample.databinding.FragmentUserInfoBinding
import ir.co.sample.di.users.DaggerUsersComponent
import ir.co.sample.viewmodel.UserInfoViewModel
import ir.co.sample.viewmodel.UserInfoViewModelImpl
import javax.inject.Inject

class UserInfoFragment : FragmentParent<UserInfoViewModel, FragmentUserInfoBinding>() {

    @Inject
    lateinit var factory: UserInfoViewModelImpl.Factory

    override fun getResourceLayoutId(): Int = R.layout.fragment_user_info

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun getViewModelClass(): Class<UserInfoViewModel> = UserInfoViewModel::class.java

    override fun inject() {
        DaggerUsersComponent.builder()
            .repositoryComponent(getRepositoryComponent(getPreferenceManager()))
            .dataBaseModule(DataBaseModule(requireActivity().applicationContext))
            .build()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }


    override fun initView() {
        binding.userInfoVm = viewModel

        arguments?.let {
            viewModel.setUser(UserInfoFragmentArgs.fromBundle(it).user)
        }

        viewModel.sendMail.observe(this) {
            startActivity(IntentManager.sendEmail(it))
        }
    }
}