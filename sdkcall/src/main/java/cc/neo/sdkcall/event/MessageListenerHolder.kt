package cc.neo.sdkcall.event

object MessageListenerHolder {
    var listener: MessageActionListener? = null
    var callEventListener: CallEventListener? = null
}