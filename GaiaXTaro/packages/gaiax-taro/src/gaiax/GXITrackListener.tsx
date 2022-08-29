import GXTrack from "./GXTrack";



export default interface GXITrackListener {

    /**
     * Track event
     */
    onTrackEvent(gxTrack: GXTrack);
}
