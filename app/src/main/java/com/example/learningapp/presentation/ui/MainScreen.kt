package com.example.learningapp.presentation.ui

import android.graphics.drawable.shapes.Shape
import android.security.identity.AlreadyPersonalizedException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel

@Composable
fun MainScreen(viewmodel: SubjectViewModel){
    val state by viewmodel.state.collectAsState()
    var textOfSubject = remember{ mutableStateOf("")}

    LaunchedEffect(Unit) {
        viewmodel.processIntent(SubjectIntent.LoadSubject)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(20) {
            SubjectItem("Subject")
        }
    }
    Row(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(
            shape = CircleShape,
            onClick = {},
            modifier = Modifier.size(75.dp),
        ) {
            Icon(Icons.Default.Add, "Add subject button")
        }
    }

}

@Composable
fun SubjectItem(textOfSubject: String){

    ElevatedCard(modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth(1f)
        .height(100.dp),
        onClick = {},
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = textOfSubject,
                fontSize = 28.sp
            )
            Row{
                Icon(Icons.Default.Edit,"Edit button")
                Icon(Icons.Default.Delete, "Delete button")
            }
        }
    }
}

@Composable
fun AddSubjectScreen(textOfSubject: String){



}
