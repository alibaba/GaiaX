import GXTrack from './GXTrack';


export interface GXITrackListener {

  /**
   * Track event
   */
  onTrackEvent(gxTrack: GXTrack);
}

export default GXITrackListener;