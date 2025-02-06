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
import com.example.learningapp.domain.usecase.association.AddAssociationUseCase
import com.example.learningapp.domain.usecase.association.DeleteAssociationUseCase
import com.example.learningapp.domain.usecase.association.UpdateAssociationUseCase
import com.example.learningapp.domain.usecase.question.AddQuestionUseCase
import com.example.learningapp.domain.usecase.question.DeleteQuestionUseCase
import com.example.learningapp.domain.usecase.question.UpdateQuestionUseCase
import com.example.learningapp.domain.usecase.question.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.question.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.question.learnedQuestionUseCase
import com.example.learningapp.domain.usecase.question.UpdateStatisticsUseCase
import com.example.learningapp.domain.usecase.subject.AddSubjectUseCase
import com.example.learningapp.domain.usecase.subject.DeleteSubjectUseCase
import com.example.learningapp.domain.usecase.subject.GetAllSubjectsUseCase
import com.example.learningapp.domain.usecase.subject.GetSubjectByIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {

    //Statistics
    @Provides
    @Singleton
    fun providesUpdateStatisticsUseCase(repository: QuestionRepository) : UpdateStatisticsUseCase {
        return UpdateStatisticsUseCase(repository)
    }
        // Subject
    @Provides
    @Singleton
    fun providesGetSubjectByIdUseCase(repository: QuestionRepository) : GetSubjectByIdUseCase{
        return GetSubjectByIdUseCase(repository)
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
        //Associations
    @Provides
    @Singleton
    fun providesUpdateAssociationUseCase(repository: QuestionRepository) : UpdateAssociationUseCase{
        return UpdateAssociationUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesDeleteAssociationUseCase(repository: QuestionRepository) : DeleteAssociationUseCase{
        return DeleteAssociationUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesAddAssociationUseCase(repository: QuestionRepository) : AddAssociationUseCase{
        return AddAssociationUseCase(repository)
    }
        //Questions
    @Provides
    @Singleton
    fun providesUpdateQuestionUseCase(repository: QuestionRepository) : UpdateQuestionUseCase{
        return UpdateQuestionUseCase(repository)
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
    fun provideQuestionRepository(questionDao: QuestionDao, associationDao: AssociationDao, statisticsDao: StatisticsDao, subjectDao: SubjectDao): QuestionRepository {
        return QuestionRepositoryImpl(
            questionDao,
            associationDao,
            statisticsDao,
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
    fun providesAssociationDao(database: QuestionDataBase) : AssociationDao{
        return database.associationDao()
    }

    @Provides
    @Singleton
    fun providesStaticsDao(database: QuestionDataBase) : StatisticsDao{
        return database.statisticsDao()
    }

    @Provides
    @Singleton
    fun providesSubjectDao(database: QuestionDataBase) : SubjectDao{
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun providesQuestionDataBase(@ApplicationContext context: Context): QuestionDataBase {
        return Room.databaseBuilder(
            context,
            QuestionDataBase::class.java,
            "questions_db")
            .build()
    }

}