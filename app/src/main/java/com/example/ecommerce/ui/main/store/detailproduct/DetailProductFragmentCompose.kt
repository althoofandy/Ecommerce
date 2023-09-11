//package com.example.ecommerce.ui.main.store.detailproduct
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//
//class DetailProductFragmentCompose : Fragment() {
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun ScaffoldExample() {
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text(text = "Detail Produk") },
//                    navigationIcon = {
//                        IconButton(
//                            onClick = {
//                                findNavController().navigateUp()
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = null
//                            )
//                        }
//                    }
//                )
//            }, bottomBar = {
//                BottomAppBar(
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(48.dp),
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        OutlinedButton(
//                            onClick = { },
//                            modifier = Modifier
//                                .padding(end = 8.dp)
//                                .fillMaxWidth()
//                                .padding(start = 16.dp)
//                                .weight(1F, true)
//
//                        ) {
//
//                            Text(
//                                text = "Beli Sekarang",
//                                fontSize = 14.sp
//                            )
//                        }
//                        Button(
//                            onClick = { },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(end = 16.dp)
//                                .padding(start = 8.dp)
//                                .weight(1F, true)
//                        ) {
//
//                            Text(
//                                text = "+ Keranjang",
//                                fontSize = 14.sp
//                            )
//
//                        }
//                    }
//                }
//            },
//            content = { innerPadding ->
//                Column(
//                    modifier = Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                        .background(Color.White),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    ViewPager()
//                }
//            })
//    }
//
//    @OptIn(ExperimentalFoundationApi::class)
//    @Composable
//    fun ViewPager() {
//        val pageCount = 10
//        val pagerState = rememberPagerState(pageCount = {
//            4
//        })
//        HorizontalPager(
//            state = pagerState
//        ) { page ->
//            Text(
//                text = "Page: $page",
//                modifier = Modifier
//                    .fillMaxSize()
//            )
//        }
//        Row(
//            Modifier
//                .height(50.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            repeat(pageCount) { iteration ->
//                val color =
//                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
//                Box(
//                    modifier = Modifier
//                        .padding(2.dp)
//                        .clip(CircleShape)
//                        .background(color)
//                        .size(20.dp)
//
//                )
//            }
//        }
//    }
//
//
//    @Preview(showBackground = true)
//    @Composable
//    fun PreviewMessageCard() {
//        ScaffoldExample()
//    }
//}