package ir.co.sample.di

import dagger.Component
import ir.co.sample.RootApp
import ir.co.repository.di.RepositoryComponent


@AppScope
@Component(
    dependencies = [RepositoryComponent::class],
    modules = [ApplicationModule::class]
)
interface AppComponent {
    fun inject(target: RootApp)
}
