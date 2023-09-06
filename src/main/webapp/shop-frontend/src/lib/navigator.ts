import { navigates } from "./navigates"
import { Listener } from "./listener"

export const navigateListener = new Listener()

export function navigateTo(name: string) {
    searchListener.set(Function.prototype)
    navigateListener.invoke(Object.assign({}, navigates[name]))
}

export const searchListener = new Listener()

export const toolListener = new Listener()

export interface ToolbarOpt{
    icon?: string
    text: string
    disable?: boolean
    onClick?: (e: any) => void
}

export function setToolBar(toolbar: ToolbarOpt[][]) {
    toolListener.invoke(toolbar)
}