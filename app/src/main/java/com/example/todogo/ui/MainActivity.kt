package com.example.todogo.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.todogo.R
import com.example.todogo.databinding.NavHeaderBinding
import com.example.todogo.ui.authorization.SignInActivity
import com.example.todogo.ui.navfrag.AboutFragment
import com.example.todogo.ui.navfrag.AllTasksFragment
import com.example.todogo.ui.navfrag.HomeTasksFragment
import com.example.todogo.ui.navfrag.PersonalTasksFragment
import com.example.todogo.ui.navfrag.WorkTasksFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var headerView: View
    private lateinit var nameHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        headerView = navView.getHeaderView(0)
        nameHeader = headerView.findViewById(R.id.nameheader)

        val userName = FirebaseAuth.getInstance().currentUser?.email
        nameHeader.text = userName

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_open,
            R.string.navigation_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            replaceFragment(AllTasksFragment())
            navView.setCheckedItem(R.id.all_tasks)
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(R.id.nav_view)) {
        drawerLayout.closeDrawer(R.id.nav_view)
    }
    else {
        onBackPressedDispatcher.onBackPressed()
      }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.all_tasks -> replaceFragment(AllTasksFragment())
           R.id.personal -> replaceFragment(PersonalTasksFragment())
           R.id.work -> replaceFragment(WorkTasksFragment())
           R.id.home -> replaceFragment(HomeTasksFragment())
           R.id.about_us -> replaceFragment(AboutFragment())
           R.id.logout ->{ FirebaseAuth.getInstance().signOut()
               val intent = Intent(this, SignInActivity::class.java)
               startActivity(intent)
               finish()}
       }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}