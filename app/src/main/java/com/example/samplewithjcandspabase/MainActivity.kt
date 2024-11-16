package com.example.samplewithjcandspabase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.samplewithjcandspabase.CacheData.cacheData
import com.example.samplewithjcandspabase.SubActivity.Companion.createIntent
import com.example.samplewithjcandspabase.ui.theme.SampleWithJCAndSpaBaseTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * supabaseの初期化
 */
val supabase = createSupabaseClient(
    supabaseUrl = "https://xxxxxxxx.supabase.co",
    supabaseKey = "xxxxxxxxxxxxx"
) {
    install(Postgrest)
}

/**
 * MainActivity
 *
 * リスト画面
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleWithJCAndSpaBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BooksList()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BooksList() {
        var books by remember { mutableStateOf<Books>(Books()) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                Log.d("***", "リスト表示")
                books = BooksRepository().getBooks()
            }
        }
        Scaffold(
            topBar = { topBar() },
            containerColor = Color.Black,
//            contentColor = Color.Black
        ) { innerPadding ->
            LazyColumn {
                items(
                    books.books,
                    key = { books -> books.id },
                ) { book ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .height(230.dp)
                            .background(colorResource(id = R.color.book_orange))
                            .clickable {
                                startActivity(
                                    createIntent(
                                        this@MainActivity,
                                        book.id.toString()
                                    )
                                )
                            },
                        horizontalAlignment = Alignment.Start
                    ){
                        Row(){
                            Image(
                                painter = rememberImagePainter(book.image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20)),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                book.name,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                        Text(text = "Discription:")
                        Column(
                            modifier = Modifier
                                .background(color = colorResource(id = R.color.book_orange2))
                                .padding(horizontal = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .height(130.dp)
                                .width(270.dp),
                        ){
                            Text(text = book.discription.toString())
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun topBar(){
        Box(modifier = Modifier){
            SmallTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Black
                ),
                title = {
                    Text(text = "List", fontWeight = FontWeight.Bold, color = colorResource(id = R.color.book_orange))
                }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun GreetingPreview() {
        SampleWithJCAndSpaBaseTheme {
            BooksList()
        }
    }
}

/**
 * Repository
 *
 * 取得データを管理
 */
class BooksRepository {

    suspend fun getBooks(): Books {
        cacheData?.let {
            Log.d("***", "getBooks キャッシュデータ返す")
            return it
        }

        withContext(Dispatchers.IO) {
            val result = supabase.from("books").select()
            cacheData = Books(result.decodeList())
        }
        Log.d("***", "getBooks リモートデータ返す")
        return cacheData ?: Books()
    }

    fun getBook(id: Int): Books.Book = (getCache().books.filter {
        it.id == id
    }[0])

    private fun getCache() = cacheData ?: Books()
}