import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import pt.ipca.roomies.R
import pt.ipca.roomies.ui.authentication.login.LoginFragment

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Load the initial login fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment())
                .commit()
        }
    }
}
