package ir.co.sample.di.users

import dagger.Component
import ir.co.sample.ui.users.UserInfoFragment
import ir.co.sample.ui.users.UsersFragment
import ir.co.repository.di.RepositoryComponent


@UsersScope
@Component(
    dependencies = [RepositoryComponent::class],
    modules = [UsersModule::class]
)
interface UsersComponent {
    fun inject(target: UsersFragment)
    fun inject(target: UserInfoFragment)
}
