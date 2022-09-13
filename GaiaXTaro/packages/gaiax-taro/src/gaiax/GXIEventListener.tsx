import GXGesture from "./GXGesture";

export default interface GXIEventListener {

    /**
     * Gesture event
     */
    onGestureEvent(gxGesture: GXGesture);

}
