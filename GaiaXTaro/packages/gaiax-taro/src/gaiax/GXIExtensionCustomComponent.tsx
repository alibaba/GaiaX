import { ReactNode } from "react";

export default interface GXIExtensionCustomComponent {
    createComponent(componentName: string, componentData: any): ReactNode;
}
