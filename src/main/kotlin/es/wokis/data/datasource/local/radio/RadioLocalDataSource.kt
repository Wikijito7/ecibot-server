package es.wokis.data.datasource.local.radio

import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.dbo.recover.RecoverDBO

interface RadioLocalDataSource {
}

class RadioLocalDataSourceImpl(
    private val radioCollection: MongoCollection<RecoverDBO>
) : RadioLocalDataSource {

}