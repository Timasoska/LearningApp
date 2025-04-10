package com.example.learningapp.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel

@Composable
fun MainScreen(viewmodel: SubjectViewModel){
    val state by viewmodel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewmodel.processIntent(SubjectIntent.LoadSubject)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(20) {
            SubjectItem()
        }
    }
    Row(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(
            onClick = {},
            //modifier = Modifier.size(50.dp),
        ) {
            Icon(Icons.Default.Add, "")
        }
    }

}

@Composable
fun SubjectItem(){

    ElevatedCard(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(1f)
        .height(100.dp),
        onClick = {},
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Text(text = "eee",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

