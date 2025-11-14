package pml.ridho.myapplication

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import pml.ridho.myapplication.ui.theme.PMLwebviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PMLwebviewTheme {
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

    // State untuk menyimpan URL WebView yang aktif (HANYA UNTUK TAB HOME).
    var activeUrl by rememberSaveable { mutableStateOf<String?>(null) }

    // --- PERUBAHAN 1: Pisahkan state WebView ---
    // State untuk menyimpan instance WebView (untuk navigasi 'back' internal)
    var homeWebView by remember { mutableStateOf<WebView?>(null) }
    var keranjangWebView by remember { mutableStateOf<WebView?>(null) }
    // --- AKHIR PERUBAHAN 1 ---

    // --- PERUBAHAN 2: Perbaiki Back Handler ---

    // Menangani tombol kembali (back button) UNTUK TAB HOME
    BackHandler(enabled = selectedItemIndex == 0 && activeUrl != null) {
        // Jika di tab Home dan WebView aktif:
        if (homeWebView?.canGoBack() == true) {
            // 1. Prioritaskan kembali ke halaman sebelumnya di dalam WebView
            homeWebView?.goBack()
        } else {
            // 2. Jika tidak ada riwayat, tutup WebView (kembali ke tombol)
            activeUrl = null
        }
    }

    // MENANGANI TOMBOL KEMBALI (BACK BUTTON) UNTUK TAB KERANJANG
    // Aktifkan JIKA tab keranjang sedang dipilih
    BackHandler(enabled = selectedItemIndex == 2) {
        if (keranjangWebView?.canGoBack() == true) {
            // 1. Prioritaskan kembali ke halaman sebelumnya di dalam WebView
            keranjangWebView?.goBack()
        } else {
            // 2. Jika tidak ada riwayat, kembali ke tab Home (indeks 0)
            selectedItemIndex = 0
        }
    }
    // --- AKHIR PERUBAHAN 2 ---

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
                            if (selectedItemIndex == 0 && index == 0) {
                                activeUrl = null
                            }
                            selectedItemIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
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
                .fillMaxSize()
        ) {
            // Logika untuk menampilkan konten berdasarkan item yang dipilih
            when (selectedItemIndex) {
                // --- KONTEN TAB HOME (Indeks 0) ---
                0 -> {
                    if (activeUrl == null) {
                        // 1. TAMPILKAN TOMBOL JIKA activeUrl null
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(onClick = { activeUrl = "https://www.youtube.com" }) {
                                    Text(text = "Youtube")
                                }
                                Button(onClick = { activeUrl = "https://www.tiktok.com" }) {
                                    Text(text = "Tiktok")
                                }
                                Button(onClick = { activeUrl = "https://www.whatsapp.com" }) {
                                    Text(text = "Send Message")
                                }
                            }
                        }
                    } else {
                        // 2. TAMPILKAN WEBVIEW JIKA activeUrl TIDAK null
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    webViewClient = WebViewClient()
                                    loadUrl(activeUrl!!) // Muat URL yang aktif
                                    homeWebView = this // Simpan instance ke var HOME
                                }
                            },
                            update = { webView ->
                                if (webView.url != activeUrl) {
                                    webView.loadUrl(activeUrl!!)
                                }
                                homeWebView = webView // Pastikan instance ter-update
                            }
                        )
                    }
                } // Akhir dari case 0

                // --- KONTEN TAB KERANJANG (Indeks 2) ---
                2 -> {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                webViewClient = WebViewClient() // Client baru
                                loadUrl("https://www.jetbrains.com")
                                keranjangWebView = this // Simpan instance ke var KERANJANG
                            }
                        },
                        update = { webView ->
                            if (webView.url != "https://www.jetbrains.com") {
                                webView.loadUrl("https://www.jetbrains.com")
                            }
                            keranjangWebView = webView // Pastikan instance ter-update
                        }
                    )
                } // Akhir dari case 2

                // --- KONTEN TAB LAIN (Indeks 1, 3, dst.) ---
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Greeting(
                            name = items[selectedItemIndex].title
                        )
                    }
                }
            } // Akhir dari when
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
        MainScreen()
    }
}