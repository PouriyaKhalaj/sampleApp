package ir.co.avatar.di.movies

import dagger.Component
import ir.co.avatar.ui.movies.MovieInfoFragment
import ir.co.avatar.ui.movies.MoviesFragment
import ir.co.repository.di.RepositoryComponent


@MoviesScope
@Component(
    dependencies = [RepositoryComponent::class],
    modules = [MoviesModule::class]
)
interface MoviesComponent {
    fun inject(target: MoviesFragment)
    fun inject(target: MovieInfoFragment)
}
