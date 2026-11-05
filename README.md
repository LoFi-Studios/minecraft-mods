# LoFi Studios

Minecraft mods, on Fabric.

## Mods

| Mod | What it does | Download |
|---|---|---|
| [Click Out](click-out) | Click outside a container and it closes it. | [Modrinth](https://modrinth.com/mod/click-out) |

Each folder is one mod: its source, and a `README.md` saying what it does and what it deliberately does not do.

## Where to say things

- **Discord** - [join the server](https://discord.com/invite/9Cw5rHfUq7). Questions, feedback, ideas, anything that isn't a bug report.
- **Issues** - [open one](https://github.com/LoFi-Studios/minecraft-mods/issues). Bugs and requests. There is one tracker for every mod; the issue template asks which one you mean, so there is no wrong place to file it.

## Building

Java 25 and the Gradle wrapper. Nothing else to install.

```
gradlew build
```

Jars land in `<mod>/build/libs/`. The Minecraft version every mod targets is in `gradle.properties`, in one place, on purpose.

## Licence

MIT. See [LICENSE](LICENSE).
