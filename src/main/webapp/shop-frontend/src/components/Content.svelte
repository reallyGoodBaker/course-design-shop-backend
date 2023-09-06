<script lang="ts">
    import { navigateListener, toolListener, type ToolbarOpt } from "../lib/navigator"
    import type { NavigateOption } from "../lib/navigates"
    import { Button } from "fluent-svelte"

    let data: NavigateOption | null = null
    let toolbar: ToolbarOpt[][] = []

    navigateListener.set((n: NavigateOption) => {
        data = n
    })

    toolListener.set((_toolbar: ToolbarOpt[][]) => {
        toolbar = _toolbar
    })
</script>

<style>
    .c {
        width: calc(100vw - 280px);
        height: calc(100vh - 48px);
        background-color: #191919;
        border-top-left-radius: 20px;
    }

    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 72px;
        border-bottom: solid 1px rgba(255, 255, 255, 0.2);
    }

    .title {
        font-size: larger;
        color: #eee;
        margin-left: 32px;
    }

    .toolbar {
        display: flex;
        align-items: center;
        padding-right: 10px;
    }

    .divider {
        height: 42px;
        margin: 0 8px;
        width: 1.5px;
        background-color: rgba(255, 255, 255, 0.1);
    }

    .btn {
        padding: 6px;
        display: flex;
        align-items: center;
        gap: 12px;
    }
</style>

<div class="c">
    {#if data}
    <div class="header">
        <span class="title">{data.title}</span>
        <div class="toolbar">
        {#each toolbar as toolGroup, i}
            {#if i}
                <div class="divider"></div>
            {/if}
            {#each toolGroup as { icon, text, onClick, disable }}
                <Button variant='hyperlink' on:click={onClick} disabled={disable}>
                    <div class="btn">
                        <span class="icon">{icon}</span>
                        <div>{text}</div>
                    </div>
                </Button>
            {/each}
        {/each}
        </div>
    </div>
    <svelte:component this={data.component}/>
    {/if}
</div>