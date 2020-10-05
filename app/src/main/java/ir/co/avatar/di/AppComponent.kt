package ir.co.avatar.di

import dagger.Component
import ir.co.avatar.RootApp
import ir.co.repository.di.RepositoryComponent


@AppScope
@Component(
    dependencies = [RepositoryComponent::class],
    modules = [ApplicationModule::class]
)
interface AppComponent {
    fun inject(target: RootApp)
}
