package com.example.samplewithjcandspabase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.samplewithjcandspabase.ui.theme.SampleWithJCAndSpaBaseTheme

/**
 * SubActivity
 *
 * 詳細画面
 */
class SubActivity : ComponentActivity() {

    var bookData: Books.Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val id = this.intent.getStringExtra(Keys.BOOK_ID.key) ?: "0"
        bookData = BooksRepository().getBook(id.toInt())
        super.onCreate(savedInstanceState)
        setContent {
            SampleWithJCAndSpaBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    bookData?.let {
                        BookDetail(it)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BookDetail(bookData: Books.Book) {
        Scaffold(
            containerColor = Color.Black
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .height(230.dp)
                    .background(colorResource(id = R.color.book_orange)),
            ) {
                Image(
                    painter = rememberImagePainter(bookData.image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20)),
                    contentScale = ContentScale.Crop
                )
                Text(text = bookData.name)
                Text(text = bookData.discription ?: "")
            }
        }
    }

    companion object {
        fun createIntent(context: Context, id: String): Intent =
            Intent(context, SubActivity::class.java).also {
                it.putExtra(Keys.BOOK_ID.key, id)
            }
    }
}
