package com.example.learningapp.di

import android.content.Context
import androidx.room.Room
import com.example.learningapp.data.local.dao.QuestionDao
import com.example.learningapp.data.local.QuestionDataBase
import com.example.learningapp.data.local.dao.AssociationDao
import com.example.learningapp.data.local.dao.StatisticsDao
import com.example.learningapp.data.local.dao.SubjectDao
import com.example.learningapp.data.repository.QuestionRepositoryImpl
import com.example.learningapp.domain.repository.QuestionRepository
import com.example.learningapp.domain.usecase.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.learnedQuestionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {

    @Provides
    @Singleton
    fun providesGetAllQuestionsUseCase(repository: QuestionRepository) : getAllQuestionsUseCase{
        return getAllQuestionsUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesGetAllQuestionByIdUseCase(repository: QuestionRepository) : getQuestionByIdUseCase{
        return  getQuestionByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesLearnedQuestionUseCase(repository: QuestionRepository) : learnedQuestionUseCase{
        return learnedQuestionUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesQuestionDao(database: QuestionDataBase) : QuestionDao {
        return database.questionDao()
    }

    @Provides
    @Singleton
    fun providesQuestionDataBase(@ApplicationContext context: Context): QuestionDataBase {
        return Room.databaseBuilder(
            context,
            QuestionDataBase::class.java,
            "questions_db"
        ).createFromAsset("database/questions.db")
            .fallbackToDestructiveMigration() // На случай проблем с миграцией
            .build()
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(questionDao: QuestionDao, associationDao: AssociationDao, statisticsDao: StatisticsDao, subjectDao: SubjectDao): QuestionRepository {
        return QuestionRepositoryImpl(
            questionDao,
            associationDao,
            statisticsDao,
            subjectDao
        )
    }

}