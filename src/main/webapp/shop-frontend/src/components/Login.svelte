<script lang="ts">
    import { TextBox, Button, TextBlock } from 'fluent-svelte'
    import { post } from '../lib/network'

    let name = ''
    let passwd = ''

    async function login() {
        if (!name || !passwd) {
            return
        }

        if (await post('user.login', {
            name, passwd
        })) {
            location = location
        }
    }
</script>

<style>
    .c {
        position: fixed;
        top: 50%;
        left: 50%;
        padding: 20px;
        border-radius: 8px;
        transform: translate(-50%, -50%);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 240px;
        height: fit-content;
        gap: 8px;
        background-color: #191919;
    }

    .btnGroup {
        display: flex;
        justify-content: space-between;
        width: 100%;
        margin-top: 16px;
    }
</style>

<div class="c">
    <span style="align-self: start; color: #ddd; margin-bottom: 12px;">
        <TextBlock variant='title'>登录</TextBlock>
    </span>
    <TextBox bind:value={name} placeholder="用户名" />
    <TextBox bind:value={passwd} type="password" placeholder="密码" />
    <div class="btnGroup">
        <div></div>
        <Button variant='accent' on:click={login}>登录</Button>
    </div>
</div>