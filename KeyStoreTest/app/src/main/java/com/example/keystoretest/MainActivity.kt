package com.example.keystoretest

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.keystoretest.ui.theme.KeyStoreTestTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    val strInputEncrypted = "inputEncripted"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val cryptoManager = CryptoManger()
        setContent {
            KeyStoreTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var chosenEncriptionKey by remember {
                        mutableStateOf(0)
                    }

                    var messageToEncrypt by remember {
                        mutableStateOf("")
                    }

                    var messageToDecrypt by remember {
                        mutableStateOf("")
                    }

                    var messageResult by remember {
                        mutableStateOf("")
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                    {
                        TextField(value = messageToEncrypt,
                            onValueChange = {messageToEncrypt = it},
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(text = "Encrypt string")}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(value = messageToDecrypt,
                            onValueChange = {messageToDecrypt = it},
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(text = "Encrypt string")}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(value = chosenEncriptionKey.toString(),
                            onValueChange = {
                                try {
                                    chosenEncriptionKey = it.toInt()
                                }catch (e : Exception){
                                    Toast.makeText(baseContext,"Only integer numbers allowed",Toast.LENGTH_SHORT)
                                }
                                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(text = "Encrypt string")}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row {
                            Button(onClick = {
                                val bytes = messageToEncrypt.encodeToByteArray()

                                var selectedKey = when (chosenEncriptionKey){
                                    1 -> KeyName.SECRET_KEY1
                                    2 -> KeyName.SECRET_KEY2
                                    3 -> KeyName.SECRET_KEY3
                                    4 -> KeyName.SECRET_KEY4
                                    else -> KeyName.SECRET_KEY1
                                }

                                val file = getSecretDocument(selectedKey)
                                val fos = FileOutputStream(file)

                                messageToDecrypt = cryptoManager.encrypt(
                                    bytes = bytes,
                                    outputStream = fos,
                                    selectedKey
                                )?.decodeToString().toString()
                            }) {
                                Text(text = "Encrypt")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = {

                                var selectedKey = when (chosenEncriptionKey){
                                    1 -> KeyName.SECRET_KEY1
                                    2 -> KeyName.SECRET_KEY2
                                    3 -> KeyName.SECRET_KEY3
                                    4 -> KeyName.SECRET_KEY4
                                    else -> KeyName.SECRET_KEY1
                                }

                                val file = getSecretDocument(selectedKey)
                                messageResult = cryptoManager.decrypt(
                                    inputStream = FileInputStream(file),
                                    selectedKey
                                )?.decodeToString().toString()
                            }) {
                                Text(text = "Decrypt")
                            }
                        }
                        
                        Text(text = messageResult)
                    }
                    

                }
            }
        }
    }

    fun getSecretDocument(chosenKeyName: KeyName): File{
        var file = File(filesDir, chosenKeyName.valKeyName+"File.txt")
        if(!file.exists()) {
            file.createNewFile()
        }else{
            file = File(filesDir, chosenKeyName.valKeyName+"File.txt")
        }
        return file
    }

    fun clearFilesAndKeys(){
        val cryptoManager = CryptoManger()

        try {
            cryptoManager.resetKeys()
        }catch (e : Exception){
            Log.e("ErrorIsm","Error al borrar las keys: $e")
        }

        try {
            filesDir.walk().forEach {
                if(it.name.contains("secret")) {
                    it.delete()
                }
            }
        }catch (e : Exception){
            Log.e("ErrorIsm","Error al borrar los archivos encriptados: $e")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("IsmInfo", "Se ejecuto onDestroy")

        clearFilesAndKeys()
    }

    override fun onPause() {
        super.onPause()

        Log.i("IsmInfo", "Se ejecuto onPause")

        finishAndRemoveTask()
        finishAffinity()
    }

}
