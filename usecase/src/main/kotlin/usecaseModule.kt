import org.koin.dsl.module
import user.UserUsecase


val usecaseModule = module {
    single { UserUsecase(get()) }
}