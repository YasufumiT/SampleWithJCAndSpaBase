package com.example.samplewithjcandspabase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
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
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                Image(
                    painter = rememberImagePainter(bookData.image),
                    contentDescription = null,
                    modifier = Modifier
                        .height(230.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Text(
                    bookData.name,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    color = colorResource(id = R.color.book_orange3),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = with(LocalDensity.current) { 23.dp.toSp() }
                )
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .height(230.dp)
                            .background(colorResource(id = R.color.book_orange3))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(text = bookData.description ?: "")
                        }
                    }
                    Box {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "¥${bookData.price}",
                                color = colorResource(id = R.color.book_orange2),
                                fontWeight = FontWeight.Bold,
                                fontSize = with(LocalDensity.current) { 38.dp.toSp() }
                            )
                            if (bookData.star == 0) {
                                NoStarText()
                            } else {
                                LazyRow {
                                    items(count = bookData.star) {
                                        Icon(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .padding(3.dp),
                                            tint = Color.Yellow,
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NoStarText() {
        Text(
            "This book has not yet been rated.",
            color = colorResource(id = R.color.book_orange),
            fontWeight = FontWeight.SemiBold,
            fontSize = with(LocalDensity.current) { 17.dp.toSp() }
        )
    }

    companion object {
        fun createIntent(context: Context, id: String): Intent =
            Intent(context, SubActivity::class.java).also {
                it.putExtra(Keys.BOOK_ID.key, id)
            }
    }
}
