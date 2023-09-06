<script lang="ts">
    import Search from './components/Search.svelte'
    import Nav from './components/Nav.svelte'
    import Content from './components/Content.svelte'
    import Login from './components/Login.svelte'
    import { post } from './lib/network'
    import { ProgressRing } from 'fluent-svelte'

    async function isLoggedIn() {
        return await post('user.isLoggedIn')
    }
</script>

<style>
    :global(body) {
        margin: 0;
        padding: 0;
        width: 100vw;
        height: 100vh;
        color-scheme: dark;
        background-color: #1E1F29;
    }

    :global(#app) {
        width: 100vw;
        height: 100vh;
    }

    .center {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }
</style>


{#await isLoggedIn()}
<div class="center">
    <ProgressRing size={64}/>
</div>
{:then loggedIn} 
{#if loggedIn}
<Search/>
<div style="display: flex;">
    <Nav/>
    <Content/>
</div>
{:else}
<Login/>
{/if}
{/await}
