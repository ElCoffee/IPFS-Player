package com.example.wassim.musicoinplayer


import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import android.util.Log
import okio.Okio
import java.io.File
import java.io.FileNotFoundException


class IPFSDaemon(private val androidContext: Context) {

    private fun getBinaryFile() = File(androidContext.filesDir, "ipfsbin")
    private fun getRepoPath() = File(androidContext.filesDir, ".ipfs_repo")
    fun getVersionFile() = File(androidContext.filesDir, ".ipfs_version")

    fun isReady() = File(getRepoPath(), "version").exists()

    private fun getBinaryFileByABI(abi: String) = when {

        abi.toLowerCase().startsWith("x86") -> "x86"
        abi.toLowerCase().startsWith("arm") -> "arm"
        else -> "unknown"
    }


    fun run(cmd: String): Process {
        val env = arrayOf("IPFS_PATH=" + getRepoPath().absoluteFile)
        val command = getBinaryFile().absolutePath + " " + cmd

        return Runtime.getRuntime().exec(command, env)
    }

    private fun downloadFile(activity: Activity) {

        val source = Okio.buffer(Okio.source(activity.assets.open(getBinaryFileByABI(Build.CPU_ABI))))
        val sink = Okio.buffer(Okio.sink(getBinaryFile()))
        while (!source.exhausted()) {
            source.read(sink.buffer(), 1024)
        }
        source.close()
        sink.close()

    }

    fun download(activity: Activity,
                 runInit: Boolean) {

        Log.d("zboub", "Position 1");

        try {

            downloadFile(activity)
            getBinaryFile().setExecutable(true)
            Log.d("zboub", "Position 2");


            if (runInit) {
                    val exec = run("init")
                    exec.waitFor()
                    exec.inputStream.bufferedReader().readText() + exec.errorStream.bufferedReader().readText()
            }

            //afterDownloadCallback()

        } catch (e: FileNotFoundException) {
            AlertDialog.Builder(androidContext)
                    .setMessage("Unsupported architecture " + Build.CPU_ABI)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()

        }
    }



    /*
       fun calldownload(activity: Activity,ipfsDaemon: IPFSDaemon){
           val mngr = activity.getAssets()
           ipfsDaemon.download(activity, runInit = true) {
               ipfsDaemon.getVersionFile().writeText(mngr.open("version").reader().readText())
           }


       }*/

}