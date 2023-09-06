export async function post(accessor: string, data={}) {
    const [ path, name ] = accessor.split('.')
    return new Promise(async (res, rej) => {
        const body = JSON.stringify({
            method: name,
            data
        })

        const val = await (await fetch(`http://localhost:3939/api/${path}`, {
            method: 'post',
            body
        })).json()

        const done = val.done ? res : rej
        done(val.data)
    })
}