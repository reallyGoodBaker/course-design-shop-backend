<script lang="ts">
    import { TextBox } from 'fluent-svelte'
    import { searchListener } from '../lib/navigator'

    let value = ''

    function handle(type: string) {
        searchListener.invoke(value, type)
    }
</script>

<style>
    .input {
        width: 480px;
        padding: 8px;
    }

    .c {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
    }
</style>

<div class="c">
    <div class="input">
        <TextBox
            bind:value
            on:input={() => handle('input')}
            on:change={() => handle('change')}
            type="search"
            placeholder="搜索" />    
    </div>
</div>