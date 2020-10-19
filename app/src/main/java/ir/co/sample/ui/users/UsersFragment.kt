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
import ir.co.sample.databinding.FragmentUsersBinding
import ir.co.sample.di.users.DaggerUsersComponent
import ir.co.sample.viewmodel.UsersViewModel
import ir.co.sample.viewmodel.UsersViewModelImpl
import kotlinx.android.synthetic.main.fragment_users.*
import javax.inject.Inject

class UsersFragment : FragmentParent<UsersViewModel, FragmentUsersBinding>() {

    private val moviesAdapter: RvAdapterUsers by lazy {
        RvAdapterUsers { user, image, name ->
            val extras = FragmentNavigatorExtras(
                image to "avatar_element",
                name to "name_element"
            )
            findNavController().navigate(UsersFragmentDirections.actionMoviesFragmentToMovieInfoFragment(user), extras)
        }
    }

    @Inject
    lateinit var factory: UsersViewModelImpl.Factory

    override fun getResourceLayoutId(): Int = R.layout.fragment_users

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun getViewModelClass(): Class<UsersViewModel> = UsersViewModel::class.java

    override fun inject() {
        DaggerUsersComponent.builder()
            .repositoryComponent(getRepositoryComponent(getPreferenceManager()))
            .dataBaseModule(DataBaseModule(requireActivity().applicationContext))
            .build()
            .inject(this)
    }

    override fun initView() {
        binding.usersVm = viewModel

        refresh.setOnRefreshListener {
            viewModel.onRefresh()
        }

        initRecyclerView()
        viewModel.getUsersFromDb().observe(this) { items ->
            moviesAdapter.submitList(items)
        }

        viewModel.getUsers().observe(this) { items ->
            moviesAdapter.submitList(items)
        }

        if (justFirstInitData) {
            viewModel.onCreateDone()
            justFirstInitData = false
        }
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvItems.adapter = moviesAdapter
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