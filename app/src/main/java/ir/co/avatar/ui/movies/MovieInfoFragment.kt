package ir.co.avatar.ui.movies

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ir.co.avatar.R
import ir.co.avatar.databinding.FragmentMovieInfoBinding
import ir.co.avatar.di.movies.DaggerMoviesComponent
import ir.co.avatar.viewmodel.MovieInfoViewModel
import ir.co.avatar.viewmodel.MovieInfoViewModelImpl
import ir.co.common.base.FragmentParent
import ir.co.repository.di.getRepositoryComponent
import kotlinx.android.synthetic.main.fragment_movie_info.*
import javax.inject.Inject

class MovieInfoFragment : FragmentParent<MovieInfoViewModel, FragmentMovieInfoBinding>() {

    @Inject
    lateinit var factory: MovieInfoViewModelImpl.Factory

    override fun getResourceLayoutId(): Int = R.layout.fragment_movie_info

    override fun getFactory(): ViewModelProvider.Factory = factory

    override fun getViewModelClass(): Class<MovieInfoViewModel> = MovieInfoViewModel::class.java

    override fun inject() {
        DaggerMoviesComponent.builder()
            .repositoryComponent(getRepositoryComponent(getPreferenceManager()))
            .build()
            .inject(this)
    }

    override fun initView() {
        binding.movieInfoVm = viewModel


        arguments?.let {
            val movie = MovieInfoFragmentArgs.fromBundle(it).movie
            viewModel.getMovieInfo(movie)
            toolbar.title = movie.title
        }

        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }
}