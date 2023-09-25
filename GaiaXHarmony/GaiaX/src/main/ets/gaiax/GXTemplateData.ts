import GXIEventListener from './GXIEventListener';
import GXITrackListener from './GXITrackListener';

export class GXTemplateData {
  // JSON Data
  templateData: any;

  /**
   * @suppress
   */
  scrollIndex: number = -1;

  /**
   * Event listener
   */
  eventListener: GXIEventListener = null;

  /**
   * Track listener
   */
  trackListener: GXITrackListener = null;
}

export default GXTemplateData;