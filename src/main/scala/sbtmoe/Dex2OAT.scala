package sbtmoe

import sbt._

private object Dex2OAT {
  def compile(
    dex2oatExec: File,
    dexFiles: Seq[File],
    oatFile: File,
    imageFile: File,
    imageClassesFile: File,
    instructionSet: InstructionSet,
    debugInfo: Boolean,
    optimize: Boolean,
    imageBase: Option[Long] = None,
    streams: sbt.Keys.TaskStreams
  ) = {
    val args = Seq.newBuilder[String]

    args += dex2oatExec.absolutePath

    args += "--dex-file=" + dexFiles.view.map { _.absolutePath }.mkString(":")
    args += "--oat-file=" + oatFile.absolutePath
    args += "--image=" + imageFile.absolutePath
    args += "--image-classes=" + imageClassesFile.absolutePath

    args += "--instruction-set=" + instructionSet.dex2oatName

    if(debugInfo) {
      args += "--generate-debug-info"
    } else {
      args += "--no-generate-debug-info"
    }

    if(optimize) {
      args += "--compiler-backend=Optimizing"
    } else {
      args += "--compiler-backend=Quick"
    }

    imageBase.foreach { base =>
      args += "--base=0x" + base.toHexString
    }

    val ret = Process(args.result()).!

    if(ret != 0)
      throw new RuntimeException(s"dex2oat failed with return code $ret")
  }
}
