package ir.co.avatar.di.movies

import dagger.Module
import dagger.Provides
import ir.co.avatar.viewmodel.MovieInfoViewModelImpl
import ir.co.avatar.viewmodel.MoviesViewModelImpl
import ir.co.common.helper.SettingManager
import ir.co.repository.repositories.MoviesRepository
import ir.co.repository.repositories.MoviesRepositoryImpl
import retrofit2.Retrofit

@Module
class MoviesModule {

    @Provides
    @MoviesScope
    fun provideMoviesRepository(retrofit: Retrofit): MoviesRepository {
        return MoviesRepositoryImpl(retrofit)
    }

    @Provides
    @MoviesScope
    fun provideMoviesFactory(
        settingManager: SettingManager,
        moviesRepository: MoviesRepository
    ): MoviesViewModelImpl.Factory {
        return MoviesViewModelImpl.Factory(
            settingManager = settingManager,
            moviesRepository = moviesRepository
        )
    }

    @Provides
    @MoviesScope
    fun provideMovieInfoFactory(
        settingManager: SettingManager,
        moviesRepository: MoviesRepository
    ): MovieInfoViewModelImpl.Factory {
        return MovieInfoViewModelImpl.Factory(
            settingManager = settingManager,
            moviesRepository = moviesRepository
        )
    }
}