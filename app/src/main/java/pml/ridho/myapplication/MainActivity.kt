package pml.ridho.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pml.ridho.myapplication.ui.theme.PMLwebviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PMLwebviewTheme {
                // Memanggil MainScreen baru kita
                MainScreen()
            }
        }
    }
}

// Data class untuk menyimpan informasi item navigasi
data class NavItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk TopAppBar
@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    // Daftar item untuk bottom navigation
    val items = listOf(
        NavItem(title = "Home", icon = Icons.Default.Home),
        NavItem(title = "Lokasi", icon = Icons.Default.LocationOn),
        NavItem(title = "Keranjang", icon = Icons.Default.ShoppingCart),
        NavItem(title = "Profile", icon = Icons.Default.Person)
    )

    // State untuk melacak item yang sedang dipilih
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Top App Bar dengan nama aplikasi
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                }
            )
        },
        bottomBar = {
            // Bottom Navigation Bar
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            // Di sini Anda biasanya akan menavigasi ke layar yang berbeda
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            // Label hanya akan ditampilkan jika item ini 'selected'
                            // ini adalah perilaku default M3 untuk 4+ item
                            Text(text = item.title)
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { innerPadding ->
        // Konten utama layar
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Logika untuk menampilkan konten berdasarkan item yang dipilih
            if (selectedItemIndex == 0) {
                // Jika "Home" dipilih, tampilkan 3 tombol
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Memberi jarak antar tombol
                ) {
                    Button(onClick = { /* TODO: Tambahkan aksi untuk Youtube */ }) {
                        Text(text = "Youtube")
                    }
                    Button(onClick = { /* TODO: Tambahkan aksi untuk Tiktok */ }) {
                        Text(text = "Tiktok")
                    }
                    Button(onClick = { /* TODO: Tambahkan aksi untuk Send Message */ }) {
                        Text(text = "Send Message")
                    }
                }
            } else {
                // Jika halaman lain dipilih, tampilkan Greeting
                Greeting(
                    name = items[selectedItemIndex].title
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Halaman $name", // Menampilkan nama halaman yang aktif
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PMLwebviewTheme {
        // Mengubah preview agar menampilkan MainScreen
        MainScreen()
    }
}