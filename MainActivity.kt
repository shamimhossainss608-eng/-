package com.jadurkathi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Purple = Color(0xFF6D28D9)
private val PalePurple = Color(0xFFF3E8FF)
private val Ink = Color(0xFF24143D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JadurKathiApp() }
    }
}

private data class Product(val name: String, val price: String, val oldPrice: String, val emoji: String, val category: String)
private val sampleProducts = listOf(
    Product("স্টাইলিশ কুর্তি", "৳ ১,২৫০", "৳ ১,৬০০", "👗", "পোশাক"),
    Product("স্মার্ট ওয়াচ", "৳ ১,৮৯০", "৳ ২,২০০", "⌚", "ইলেকট্রনিকস"),
    Product("ওয়্যারলেস ইয়ারবাড", "৳ ৯৫০", "৳ ১,২০০", "🎧", "ইলেকট্রনিকস"),
    Product("স্কিন কেয়ার সেট", "৳ ৭৫০", "৳ ৯০০", "🧴", "কসমেটিকস"),
    Product("ক্যাজুয়াল স্নিকার্স", "৳ ১,৪৯০", "৳ ১,৮০০", "👟", "জুতা")
)

@Composable
fun JadurKathiApp() {
    var selectedTab by remember { mutableStateOf("হোম") }
    var cartCount by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Purple, secondary = Color(0xFF9333EA), background = Color(0xFFF8F7FB))) {
        Scaffold(
            containerColor = Color(0xFFF8F7FB),
            topBar = {
                Column(Modifier.background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪄", fontSize = 30.sp)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("জাদুর কাঠি", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Purple)
                            Text("Jadur Kathi • কেনাকাটা হোক সহজ", fontSize = 11.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { selectedTab = "কার্ট" }) {
                            BadgedBox(badge = { if (cartCount > 0) Badge { Text(cartCount.toString()) } }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "কার্ট", tint = Purple)
                            }
                        }
                    }
                    if (selectedTab == "হোম" || selectedTab == "ক্যাটাগরি") {
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("পণ্য খুঁজুন...") }, leadingIcon = { Icon(Icons.Default.Search, null) },
                            singleLine = true, shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    val tabs = listOf("হোম" to Icons.Default.Home, "ক্যাটাগরি" to Icons.Default.GridView, "ভিডিও" to Icons.Default.PlayCircle, "অর্ডার" to Icons.Default.ReceiptLong, "প্রোফাইল" to Icons.Default.Person)
                    tabs.forEach { (label, icon) ->
                        NavigationBarItem(selected = selectedTab == label, onClick = { selectedTab = label }, icon = { Icon(icon, contentDescription = label) }, label = { Text(label, fontSize = 10.sp) })
                    }
                }
            },
            snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (selectedTab) {
                    "হোম" -> HomeScreen(query, onAdd = { cartCount++ }, onVideos = { selectedTab = "ভিডিও" }, onMessage = { message = it })
                    "ক্যাটাগরি" -> CategoriesScreen(query, onCategory = { query = it; selectedTab = "হোম" })
                    "ভিডিও" -> VideoScreen(onBuy = { cartCount++; message = "পণ্যটি কার্টে যোগ হয়েছে" })
                    "অর্ডার" -> OrdersScreen()
                    "প্রোফাইল" -> ProfileScreen(onSelect = { selectedTab = it })
                    "কার্ট" -> CartScreen(cartCount, onRemove = { cartCount = 0 }, onCheckout = { message = "চেকআউটের নমুনা: পেমেন্ট ও অর্ডার ব্যাকএন্ড যুক্ত করতে হবে" })
                    "বিক্রেতা প্যানেল" -> SellerScreen(onMessage = { message = it })
                    "অ্যাডমিন প্যানেল" -> AdminScreen(onMessage = { message = it })
                    "অভিযোগ" -> ComplaintScreen(onSubmit = { message = "ডেমো অভিযোগ জমা হয়েছে। বাস্তব অ্যাপে সার্ভারে সংরক্ষণ যুক্ত করতে হবে।" })
                    else -> HomeScreen(query, onAdd = { cartCount++ }, onVideos = { selectedTab = "ভিডিও" }, onMessage = { message = it })
                }
                message?.let { msg ->
                    AlertDialog(onDismissRequest = { message = null }, confirmButton = { TextButton(onClick = { message = null }) { Text("ঠিক আছে") } }, title = { Text("জাদুর কাঠি") }, text = { Text(msg) })
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(query: String, onAdd: () -> Unit, onVideos: () -> Unit, onMessage: (String) -> Unit) {
    val filtered = sampleProducts.filter { it.name.contains(query, true) || it.category.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PalePurple), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("আপনার পছন্দের সবকিছু এক অ্যাপে", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Spacer(Modifier.height(5.dp))
                    Text("পণ্য দেখুন • ভিডিওতে জানুন • নিরাপদে অর্ডার করুন", color = Color(0xFF5B3B82), fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onVideos, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("ভিডিও শপিং দেখুন") }
                }
            }
        }
        item { Text("জনপ্রিয় ক্যাটাগরি", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("👗\nপোশাক", "🧴\nকসমেটিকস", "📱\nইলেকট্রনিকস", "👟\nজুতা").forEach { cat ->
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) { Text(cat, fontSize = 12.sp, lineHeight = 20.sp) }
                    }
                }
            }
        }
        item { Text("আপনার জন্য পণ্য", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink) }
        items(filtered) { product -> ProductCard(product, onAdd) }
        item { Text("নোট: এগুলো ডেমো পণ্য। লাইভ পণ্য দেখাতে সার্ভার/ডেটাবেস যুক্ত করতে হবে।", fontSize = 11.sp, color = Color.Gray) }
    }
}

@Composable
private fun ProductCard(product: Product, onAdd: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(82.dp).background(PalePurple, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(product.emoji, fontSize = 36.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, color = Ink)
                Text(product.category, fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Text(product.price, color = Purple, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(product.oldPrice, color = Color.Gray, fontSize = 11.sp)
            }
            Button(onClick = onAdd, contentPadding = PaddingValues(horizontal = 10.dp), colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("কার্টে +", fontSize = 11.sp) }
        }
    }
}

@Composable
private fun CategoriesScreen(query: String, onCategory: (String) -> Unit) {
    val cats = listOf("পোশাক", "কসমেটিকস", "জুতা ও স্যান্ডেল", "ইলেকট্রনিকস", "মোবাইল", "কম্পিউটার ও ল্যাপটপ", "ব্লুটুথ ও ইয়ারবাড", "হোম ও লাইফস্টাইল", "বই ও স্টেশনারি")
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("সব ক্যাটাগরি", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        cats.filter { it.contains(query, true) || query.isBlank() }.forEach { cat ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onCategory(cat) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Category, null, tint = Purple); Spacer(Modifier.width(12.dp)); Text(cat, Modifier.weight(1f)); Icon(Icons.Default.ChevronRight, null) }
            }
        }
    }
}

@Composable
private fun VideoScreen(onBuy: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("ভিডিও শপিং", fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("পণ্যের ভিডিও দেখুন এবং পছন্দ হলে অর্ডার করুন।", color = Color.Gray) }
        items(sampleProducts.take(4)) { p ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Box(Modifier.fillMaxWidth().height(150.dp).background(Color(0xFF24143D), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(48.dp)); Text("ভিডিও প্রিভিউ", color = Color.White); Text(p.emoji, fontSize = 28.sp) }
                    }
                    Spacer(Modifier.height(10.dp)); Text(p.name, fontWeight = FontWeight.Bold); Text(p.price, color = Purple, fontWeight = FontWeight.Bold)
                    Button(onClick = onBuy, modifier = Modifier.fillMaxWidth()) { Text("পণ্য দেখুন / কার্টে যোগ করুন") }
                }
            }
        }
        item { Text("এটি ভিডিও ফিডের UI নমুনা; আসল ভিডিও আপলোড ও প্লেব্যাকের জন্য মিডিয়া স্টোরেজ ও ব্যাকএন্ড লাগবে।", color = Color.Gray, fontSize = 12.sp) }
    }
}

@Composable
private fun CartScreen(count: Int, onRemove: () -> Unit, onCheckout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("আমার কার্ট", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        if (count == 0) { Text("আপনার কার্ট এখন খালি।", color = Color.Gray) }
        else {
            Text("ডেমো পণ্য", fontWeight = FontWeight.SemiBold); Text("পরিমাণ: $count"); Text("আনুমানিক মোট: ৳ ${(count * 1250)}", color = Purple, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = onRemove) { Text("কার্ট খালি করুন") }
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) { Text("চেকআউট") }
        }
        Text("বাস্তব অর্ডার ও পেমেন্টের জন্য ব্যাকএন্ড যুক্ত করতে হবে।", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun OrdersScreen() {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("আমার অর্ডার", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(16.dp)) { Text("এখনো কোনো অর্ডার নেই", fontWeight = FontWeight.SemiBold); Text("অর্ডার করলে এখানে ডেলিভারির অবস্থা দেখতে পারবেন।", color = Color.Gray) } }
    }
}

@Composable
private fun ProfileScreen(onSelect: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("আমার প্রোফাইল", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = PalePurple)) { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.AccountCircle, null, tint = Purple, modifier = Modifier.size(44.dp)); Spacer(Modifier.width(12.dp)); Column { Text("স্বাগতম!", fontWeight = FontWeight.Bold); Text("লগইন করলে আপনার তথ্য দেখা যাবে", fontSize = 12.sp) } } }
        ProfileAction("বিক্রেতা প্যানেল", Icons.Default.Storefront) { onSelect("বিক্রেতা প্যানেল") }
        ProfileAction("অভিযোগ / সহায়তা", Icons.Default.SupportAgent) { onSelect("অভিযোগ") }
        ProfileAction("অ্যাডমিন প্যানেল (ডেমো)", Icons.Default.AdminPanelSettings) { onSelect("অ্যাডমিন প্যানেল") }
        Text("সতর্কতা: এই ডেমোতে অ্যাডমিন স্ক্রিনে প্রবেশের বাস্তব অনুমতি পরীক্ষা নেই। প্রকাশের আগে সার্ভার-সাইড ভূমিকা ও লগইন নিরাপত্তা আবশ্যক।", fontSize = 12.sp, color = Color(0xFF9A3412))
    }
}

@Composable
private fun ProfileAction(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Purple); Spacer(Modifier.width(12.dp)); Text(title, Modifier.weight(1f)); Icon(Icons.Default.ChevronRight, null) } }
}

@Composable
private fun SellerScreen(onMessage: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("বিক্রেতা প্যানেল", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("পণ্য ও ভিডিও প্রকাশের আগে আপনার তথ্য যাচাই করা হবে।", color = Color.Gray)
        listOf("নতুন পণ্য যোগ করুন", "পণ্যের ছবি আপলোড", "পণ্যের ভিডিও আপলোড", "আমার অর্ডার", "বিক্রির হিসাব").forEach { label ->
            Button(onClick = { onMessage("$label: এই ডেমোতে UI তৈরি আছে; ফিচারটি চালু করতে ডেটাবেস/ফাইল আপলোড যুক্ত করতে হবে।") }, modifier = Modifier.fillMaxWidth()) { Text(label) }
        }
    }
}

@Composable
private fun AdminScreen(onMessage: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("অ্যাডমিন প্যানেল", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Purple)
        Text("অ্যাডমিনের জন্য সংরক্ষিত নিয়ন্ত্রণসমূহ", color = Color.Gray)
        listOf("বিক্রেতা যাচাই", "পণ্য অনুমোদন", "ভিডিও অনুমোদন", "অভিযোগ পর্যালোচনা", "সন্দেহজনক অ্যাকাউন্ট স্থগিত", "অর্ডার ও রিফান্ড অনুরোধ").forEach { label ->
            Card(Modifier.fillMaxWidth().clickable { onMessage("$label: বাস্তব অ্যাডমিন অ্যাকশন চালু করতে সুরক্ষিত ব্যাকএন্ড প্রয়োজন।") }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.VerifiedUser, null, tint = Purple); Spacer(Modifier.width(10.dp)); Text(label, Modifier.weight(1f)); Icon(Icons.Default.ChevronRight, null) }
            }
        }
    }
}

@Composable
private fun ComplaintScreen(onSubmit: () -> Unit) {
    var orderId by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("অভিযোগ ও সহায়তা", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("সমস্যার বিবরণ দিন। অর্ডার নম্বর থাকলে যোগ করুন।", color = Color.Gray)
        OutlinedTextField(value = orderId, onValueChange = { orderId = it }, label = { Text("অর্ডার নম্বর (ঐচ্ছিক)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = detail, onValueChange = { detail = it }, label = { Text("অভিযোগের বিবরণ") }, modifier = Modifier.fillMaxWidth().height(140.dp))
        Button(onClick = onSubmit, enabled = detail.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("অভিযোগ জমা দিন") }
    }
}
