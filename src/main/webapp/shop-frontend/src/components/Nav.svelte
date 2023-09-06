<script lang="ts">
    import { Button, ContentDialog, ListItem } from 'fluent-svelte'
    import { post } from '../lib/network'
    import { navigateTo } from '../lib/navigator'
    import { onMount } from 'svelte'
    import { navigates } from '../lib/navigates'

    function doLogout() {
        post('user.logout')
        location = location
    }

    function logout() {
        open = true
    }

    let selected = 0

    onMount(() => {
        navigateTo('shop')
    })

    function nav(i: number, name: string) {
        selected = i
        navigateTo(name)
    }

    let open = false
</script>

<style>
    .c {
        width: 280px;
        margin: 32px 0 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }

    .col {
        display: flex;
        flex-direction: column;
    }

    .icon {
        margin-right: 16px;
    }
</style>

<div class="c">
    <div class="col">
        {#each Object.entries(navigates) as [name, { icon, title }], i}
        <ListItem selected={i == selected} on:click={() => nav(i, name)}><span class="icon">{icon}</span>{title}</ListItem>
        {/each}
    </div>
    <ListItem on:click={logout}><span class="icon">{'\uf3b1'}</span>登出</ListItem>
</div>

<ContentDialog bind:open title="确认登出">
    登出后需要重新登录
    <svelte:fragment slot="footer">
        <Button variant="accent" on:click={() => {
            open = false
            doLogout()
        }}>
            是
        </Button>
        <Button on:click={() => open = false}>
            否
        </Button>
    </svelte:fragment>
</ContentDialog>