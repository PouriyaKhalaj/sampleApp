package ir.co.avatar.ui.movies

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.co.avatar.R
import ir.co.avatar.adapters.RvAdapterMovies
import ir.co.avatar.databinding.FragmentMoviesBinding
import ir.co.avatar.di.movies.DaggerMoviesComponent
import ir.co.avatar.viewmodel.MoviesViewModel
import ir.co.avatar.viewmodel.MoviesViewModelImpl
import ir.co.common.base.FragmentParent
import ir.co.repository.di.getRepositoryComponent
import kotlinx.android.synthetic.main.fragment_movies.*
import javax.inject.Inject

class MoviesFragment : FragmentParent<MoviesViewModel, FragmentMoviesBinding>() {

    private val moviesAdapter: RvAdapterMovies by lazy {
        RvAdapterMovies {
            findNavController().navigate(
                MoviesFragmentDirections.actionMoviesFragmentToMovieInfoFragment(it)
            )
        }
    }

    @Inject
    lateinit var factory: MoviesViewModelImpl.Factory

    override fun getResourceLayoutId(): Int = R.layout.fragment_movies

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun getViewModelClass(): Class<MoviesViewModel> = MoviesViewModel::class.java

    override fun inject() {
        DaggerMoviesComponent.builder()
            .repositoryComponent(getRepositoryComponent(getPreferenceManager()))
            .build()
            .inject(this)
    }

    override fun initView() {
        initRecyclerView()
        toolbar.setTitle(R.string.str_movies)
        refresh.setOnRefreshListener {
            viewModel.onRefresh()
        }

        viewModel.getMovies().observe(this, Observer { items ->
            moviesAdapter.submitList(items)
        })

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