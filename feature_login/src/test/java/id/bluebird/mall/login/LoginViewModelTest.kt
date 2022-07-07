package id.bluebird.mall.login

//
//@ExperimentalCoroutinesApi
//@ExtendWith(TestCoroutineRule::class)
//class LoginViewModelTest {
//
//    private val loginCase: Login = mockk()
//
//    private lateinit var mLoginVM: LoginViewModel
//    private val loginStateObserver: Observer<LoginState> = mockk()
//
//    @BeforeEach
//    fun setup() {
//        mLoginVM = LoginViewModel(loginCase)
//        mLoginVM.loginState.observeForever(loginStateObserver)
//    }
//
//    @AfterEach
//    fun tearDown() {
//        mLoginVM.loginState.removeObserver(loginStateObserver)
//    }
//
//    @Test
//    fun `doLogin Result login Success`() = runTest {
//        // Pre
//        mLoginVM.username.value = "abc"
//        mLoginVM.password.value = "abc"
//        val token = "token"
//
//        // Mock
//        every { loginCase.invoke(any(), any()) } returns flow {
////            emit(CasesResult.OnSuccess(token))
//        }
//        justRun { loginStateObserver.onChanged(any()) }
//
//        // Execute
//        mLoginVM.login()
//        delay(200)
//
//        // Result
//        verify(atLeast = 1) { loginStateObserver.onChanged(CommonState.Progress) }
//        verify { loginStateObserver.onChanged(LoginState.Success) }
//    }
//
//    @Test
//    fun `doLogin Result login Error with GeneralError`() = runTest {
//        // Pre
//        mLoginVM.username.value = "abc"
//        mLoginVM.password.value = "abc"
//        val notFound = GeneralError.NotFound("not found")
//
//        // Mock
//        every { loginCase.invoke(any(), any()) } returns flow {
////            emit(CasesResult.OnError(notFound))
//        }
//        justRun { loginStateObserver.onChanged(any()) }
//
//        // Execute
//        mLoginVM.login()
//        delay(200)
//
//        // Result
//        verify(atLeast = 1) { loginStateObserver.onChanged(CommonState.Progress) }
//        verify { loginStateObserver.onChanged(notFound) }
//    }
//
//    @Test
//    fun `doLogin Result login Error with Throw Exception`() = runTest {
//        // Pre
//        mLoginVM.username.value = "abc"
//        mLoginVM.password.value = "abc"
//        val exception = NullPointerException("")
//
//        // Mock
//        every { loginCase.invoke(any(), any()) } returns flow {
//            throw exception
//        }
//        justRun { loginStateObserver.onChanged(any()) }
//
//        // Execute
//        mLoginVM.login()
//        delay(200)
//
//        // Result
//        verify(atLeast = 1) { loginStateObserver.onChanged(CommonState.Progress) }
//        verify { loginStateObserver.onChanged(CommonState.Error(exception)) }
//    }
//
//    @Test
//    fun `callPhone Result StatePhone is called`() = runTest {
//        // Mock
//        justRun { loginStateObserver.onChanged(any()) }
//
//        // Execute
//        mLoginVM.callPhone()
//        delay(500)
//
//        // Result
//        verify { loginStateObserver.onChanged(LoginState.Phone) }
//    }
//}
