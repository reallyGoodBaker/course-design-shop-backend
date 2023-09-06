import type { ToolbarOpt } from "./navigator";

export class Tool implements ToolbarOpt{
    constructor(
        public text: string,
        public icon = '',
        public disable = false,
        public onClick = () => {}
    ) {}
}