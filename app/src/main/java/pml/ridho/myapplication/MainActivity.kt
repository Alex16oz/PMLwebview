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

    // State untuk instance WebView
    var homeWebView by remember { mutableStateOf<WebView?>(null) }
    var keranjangWebView by remember { mutableStateOf<WebView?>(null) }
    var profileWebView by remember { mutableStateOf<WebView?>(null) }

    // Menangani tombol kembali (back button) UNTUK TAB HOME
    BackHandler(enabled = selectedItemIndex == 0 && activeUrl != null) {
        if (homeWebView?.canGoBack() == true) {
            homeWebView?.goBack()
        } else {
            activeUrl = null
        }
    }

    // MENANGANI TOMBOL KEMBALI (BACK BUTTON) UNTUK TAB KERANJANG
    BackHandler(enabled = selectedItemIndex == 2) {
        if (keranjangWebView?.canGoBack() == true) {
            keranjangWebView?.goBack()
        } else {
            selectedItemIndex = 0 // Kembali ke Home
        }
    }

    // MENANGANI TOMBOL KEMBALI (BACK BUTTON) UNTUK TAB PROFILE
    BackHandler(enabled = selectedItemIndex == 3) {
        if (profileWebView?.canGoBack() == true) {
            profileWebView?.goBack()
        } else {
            selectedItemIndex = 0 // Kembali ke Home
        }
    }

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
                        // 1. TAMPILKAN TOMBOL
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
                        // 2. TAMPILKAN WEBVIEW
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true // <-- TAMBAHKAN INI
                                    webViewClient = WebViewClient()
                                    loadUrl(activeUrl!!)
                                    homeWebView = this
                                }
                            },
                            update = { webView ->
                                if (webView.url != activeUrl) {
                                    webView.loadUrl(activeUrl!!)
                                }
                                homeWebView = webView
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
                                settings.domStorageEnabled = true // <-- TAMBAHKAN INI
                                webViewClient = WebViewClient()
                                loadUrl("https://www.jetbrains.com")
                                keranjangWebView = this
                            }
                        },
                        update = { webView ->
                            if (webView.url != "https://www.jetbrains.com") {
                                webView.loadUrl("https://www.jetbrains.com")
                            }
                            keranjangWebView = webView
                        }
                    )
                } // Akhir dari case 2

                // --- KONTEN TAB PROFILE (Indeks 3) ---
                3 -> {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true // <-- TAMBAHKAN INI
                                webViewClient = WebViewClient()
                                loadUrl("https://myaccount.google.com/")
                                profileWebView = this // Simpan instance
                            }
                        },
                        update = { webView ->
                            val targetUrl = "https://myaccount.google.com/"
                            if (webView.url != targetUrl) {
                                webView.loadUrl(targetUrl)
                            }
                            profileWebView = webView // Pastikan instance ter-update
                        }
                    )
                } // Akhir dari case 3

                // --- KONTEN TAB LAIN (Sekarang hanya Indeks 1 / Lokasi) ---
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