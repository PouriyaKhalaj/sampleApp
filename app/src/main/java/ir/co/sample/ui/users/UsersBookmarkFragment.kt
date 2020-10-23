package ir.co.sample.ui.users

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.co.common.base.FragmentParent
import ir.co.repository.di.DataBaseModule
import ir.co.repository.di.getRepositoryComponent
import ir.co.sample.R
import ir.co.sample.adapters.RvAdapterUsers
import ir.co.sample.databinding.FragmentUsersBookmarkBinding
import ir.co.sample.di.users.DaggerUsersComponent
import ir.co.sample.viewmodel.UsersBookmarkViewModel
import ir.co.sample.viewmodel.UsersBookmarkViewModelImpl
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsersBookmarkFragment :
    FragmentParent<UsersBookmarkViewModel, FragmentUsersBookmarkBinding>() {
    private val usersAdapter: RvAdapterUsers by lazy {
        RvAdapterUsers { user, image, name ->
            val extras = FragmentNavigatorExtras(
                image to "avatar_element",
                name to "name_element"
            )
            findNavController().navigate(
                UsersBookmarkFragmentDirections.actionUsersBookmarkFragmentToUserInfoFragment(user),
                extras
            )
        }
    }


    @Inject
    lateinit var factory: UsersBookmarkViewModelImpl.Factory

    override fun getResourceLayoutId(): Int = R.layout.fragment_users_bookmark

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun getViewModelClass(): Class<UsersBookmarkViewModel> =
        UsersBookmarkViewModel::class.java

    override fun inject() {
        DaggerUsersComponent.builder()
            .dataBaseModule(DataBaseModule(requireActivity().baseContext))
            .repositoryComponent(getRepositoryComponent(getPreferenceManager()))
            .build()
            .inject(this)
    }

    override fun initView() {
        binding.usersBookmarkVm = viewModel

        refresh.setOnRefreshListener {
            viewModel.onRefresh()
        }

        initRecyclerView()

        viewModel.getUsersFromDb().observe(this) { items ->
            usersAdapter.submitList(items)
        }

        GlobalScope.launch {
            delay(400)
            viewModel.onCreateDone()
        }
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvItems.adapter = usersAdapter
    }

    override fun showProgress() {
        super.showProgress()
        refresh.isRefreshing = true
    }

    override fun hideProgress() {
        super.hideProgress()
        refresh.isRefreshing = false
    }
}