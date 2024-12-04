package com.example.samplewithjcandspabase

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        var books by remember { mutableStateOf(Books()) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                books = BooksRepository(applicationContext).getBooks()
            }
        }
        Scaffold(
            topBar = { TopBar() },
            containerColor = colorResource(id = R.color.black),
        ) { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    SearchBar(innerPadding)
                }
                items(
                    books.books,
                    key = { books -> books.id },
                ) { book ->
                    BookItem(book = book)
                }
            }
        }
    }

    @Composable
    private fun SearchBar(innerPadding: PaddingValues) {
        var text by rememberSaveable { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = "Searching for...") },
            trailingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier
                        .size(53.dp)
                        .padding(6.dp)
                )
            },
            shape = RoundedCornerShape(50.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = colorResource(id = R.color.white),
                focusedBorderColor = colorResource(id = R.color.white),
                unfocusedBorderColor = colorResource(id = R.color.white),
                textColor = Color(android.graphics.Color.parseColor("#5e5e5e")),
                unfocusedLabelColor = Color(android.graphics.Color.parseColor("#5e5e5e"))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = RoundedCornerShape(50.dp))
                .background(colorResource(id = R.color.white), CircleShape)
        )
    }

    @Composable
    private fun BookItem(book: Books.Book) {
        Box {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .height(160.dp)
                    .background(colorResource(id = R.color.black))
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.book_orange3),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        startActivity(
                            createIntent(
                                this@MainActivity,
                                book.id.toString()
                            )
                        )
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter(book.image),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text(
                                "title:",
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                                color = colorResource(id = R.color.book_orange),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                book.name,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.book_orange2),
                                fontSize = with(LocalDensity.current) { 23.dp.toSp() }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(colorResource(id = R.color.book_orange3))
                        ) {
                            Text(
                                modifier = Modifier.padding(10.dp),
                                text = book.description.toString(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = colorResource(id = R.color.black)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBar() {
        SmallTopAppBar(
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = colorResource(id = R.color.black)
            ),
            title = {
                Text(
                    text = "Book List",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.book_orange3)
                )
            }
        )
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
class BooksRepository(
    context: Context? = null
) {

    /**
     * supabaseの初期化
     */
    private val supabase = context?.let {
        createSupabaseClient(
            supabaseUrl = it.getString(R.string.supabase_url),
            supabaseKey = it.getString(R.string.supabase_key)
        ) {
            install(Postgrest)
        }
    }

    suspend fun getBooks(): Books {
        cacheData?.let {
            return it
        }

        withContext(Dispatchers.IO) {
            cacheData = try {
                val result = supabase?.from("books")?.select()
                if (result != null && result.data.isNotEmpty()) {
                    Books(result.decodeList())
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.d("log", "Exception: $e")
                null
            }
        }
        return cacheData ?: Books()
    }

    fun getBook(id: Int): Books.Book = (getCache().books.filter {
        it.id == id
    }[0])

    private fun getCache() = cacheData ?: Books()
}