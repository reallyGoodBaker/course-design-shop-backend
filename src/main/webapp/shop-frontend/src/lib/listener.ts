export class Listener {
    private _callback: Function = Function.prototype

    set(func: Function) {
        this._callback = func
    }

    invoke(...args: any[]) {
        return this._callback.apply(null, args)
    }
}