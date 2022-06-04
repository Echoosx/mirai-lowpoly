package org.echoosx.mirai.plugin.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import lowpoly.LowPoly
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.firstIsInstanceOrNull
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.echoosx.mirai.plugin.MiraiLowPoly
import java.net.URL
import javax.imageio.ImageIO

object GenerateLowPolyCommand:SimpleCommand(
    MiraiLowPoly,
    "lowpoly","晶格化", description = "将图片进行晶格化"
) {
    private val logger get() = MiraiLowPoly.logger

    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<MessageEvent>.handle(accuracy:Int = 100,pointCount:Int = 200){
        try{
            val image = ImageIO.read(URL(this.fromEvent.getOrWaitImage()?.queryUrl()))

            val resource = LowPoly.generate(
                image,
                accuracy,
                1F,
                true,
                "png",
                false,
                pointCount
            ).toByteArray().toExternalResource()

            subject?.sendImage(resource)
            withContext(Dispatchers.IO) {
                resource.close()
            }
        }catch (e:Throwable){
            logger.error(e)
            subject?.sendMessage("生成失败")
        }
    }

    private suspend fun MessageEvent.getOrWaitImage(): Image? {
        return (message.takeIf { m -> m.contains(Image) } ?: runCatching {
            subject.sendMessage("请在30s内发送图片")
            nextMessage(30_000) { event -> event.message.contains(Image) }
        }.getOrElse { e ->
            when (e) {
                is TimeoutCancellationException -> {
                    subject.sendMessage(PlainText("超时未发送").plus(message.quote()))
                    return null
                }
                else -> throw e
            }
        }).firstIsInstanceOrNull<Image>()
    }
}