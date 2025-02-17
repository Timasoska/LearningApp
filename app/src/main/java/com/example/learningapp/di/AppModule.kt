package com.example.learningapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.learningapp.data.local.dao.QuestionDao
import com.example.learningapp.data.local.QuestionDataBase
import com.example.learningapp.data.local.dao.SubjectDao
import com.example.learningapp.data.repository.QuestionRepositoryImpl
import com.example.learningapp.domain.repository.QuestionRepository
import com.example.learningapp.domain.usecase.question.AddQuestionUseCase
import com.example.learningapp.domain.usecase.question.DeleteQuestionUseCase
import com.example.learningapp.domain.usecase.question.GetQuestionsBySubjectUseCase
import com.example.learningapp.domain.usecase.question.UpdateQuestionUseCase
import com.example.learningapp.domain.usecase.question.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.question.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.question.learnedQuestionUseCase
import com.example.learningapp.domain.usecase.subject.AddSubjectUseCase
import com.example.learningapp.domain.usecase.subject.DeleteSubjectUseCase
import com.example.learningapp.domain.usecase.subject.GetAllSubjectsUseCase
import com.example.learningapp.domain.usecase.subject.GetSubjectByIdUseCase
import com.example.learningapp.domain.usecase.subject.UpdateSubjectUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {

        // Subject
    @Provides
    @Singleton
    fun providesGetSubjectByIdUseCase(repository: QuestionRepository) : GetSubjectByIdUseCase{
        return GetSubjectByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesUpdateSubjectUseCase(repository: QuestionRepository): UpdateSubjectUseCase {
        return UpdateSubjectUseCase(repository)
    }


    @Provides
    @Singleton
    fun providesGetAllSubjectsUseCase(repository: QuestionRepository) : GetAllSubjectsUseCase{
        return GetAllSubjectsUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesDeleteSubjectUseCase(repository: QuestionRepository) : DeleteSubjectUseCase{
        return DeleteSubjectUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesAddSubjectUseCase(repository: QuestionRepository) : AddSubjectUseCase{
        return AddSubjectUseCase(repository)
    }

        //Questions
    @Provides
    @Singleton
    fun providesUpdateQuestionUseCase(repository: QuestionRepository) : UpdateQuestionUseCase{
        return UpdateQuestionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetQuestionsBySubjectUseCase(repository: QuestionRepository): GetQuestionsBySubjectUseCase {
        return GetQuestionsBySubjectUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesDeleteQuestionUseCase(repository: QuestionRepository) : DeleteQuestionUseCase{
        return DeleteQuestionUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesAddQuestionUseCase(repository: QuestionRepository) : AddQuestionUseCase{
        return AddQuestionUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesGetAllQuestionsUseCase(repository: QuestionRepository) : getAllQuestionsUseCase {
        return getAllQuestionsUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesGetAllQuestionByIdUseCase(repository: QuestionRepository) : getQuestionByIdUseCase {
        return  getQuestionByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesLearnedQuestionUseCase(repository: QuestionRepository) : learnedQuestionUseCase {
        return learnedQuestionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(questionDao: QuestionDao, subjectDao: SubjectDao): QuestionRepository {
        return QuestionRepositoryImpl(
            questionDao,
            subjectDao
        )
    }
        //DAO
    @Provides
    @Singleton
    fun providesQuestionDao(database: QuestionDataBase) : QuestionDao {
        return database.questionDao()
    }


    @Provides
    @Singleton
    fun providesSubjectDao(database: QuestionDataBase) : SubjectDao{
        return database.subjectDao()
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Добавляем колонку subjectId в таблицу вопросов
            database.execSQL("ALTER TABLE questions ADD COLUMN subjectId INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun providesQuestionDataBase(@ApplicationContext context: Context): QuestionDataBase {
        return Room.databaseBuilder(
            context,
            QuestionDataBase::class.java,
            "questions_db")
            .addMigrations(MIGRATION_2_3)
            .build()
    }

}