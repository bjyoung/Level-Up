# Level Up

Level Up is an Android app for tracking to-do tasks with a classic arcade game aesthetic.

## How to install?

1. TBD

## Setup Development Environment (Windows)

1. Download and install [Android Studio](https://developer.android.com/studio)
1. Clone the [repository](https://github.com/bjyoung/Level-Up)
    1. Open Android Studio
    1. Click on 'Get from VCS' option > GitHub > Use token...
    1. Have an existing repo admin generate a token with repo, workflow and read:org access
    1. Paste the token in the 'Token' field
    1. Choose 'Level-Up' project > Clone
        1. May have to run the command again because sometimse nothing happens.
1. Setup project
    1. Setup android emulator: open Device Manager > Create device > select 'Pixel 2 XL' > Next > choose 'R', API level 30 > Next > Finish
1. Setup markdown file editor
    1. Download [Visual Studio Code](https://code.visualstudio.com/download)
    1. Open Visual Studio Code and go to Extensions section (four squares icon in left bar)
    1. Search for the 'markdownlint' extension and install it
1. Setup Android Studio emulator command line tools
    1. Open Android Studio and go to File > Project Structure > SDK Location > copy path under 'Android SDK location'
    1. Create a system environment variable ANDROID_HOME set to the copied path
    1. Edit the PATH system environment variable and add the paths below
        1. %ANDROID_HOME%\tools
        1. %ANDROID_HOME%\tools\bin
        1. %ANDROID_HOME%\platform-tools
    1. Click OK out of the system windows to save the changes
    1. Open the command prompt, type in 'adb' and press ENTER to see if 'adb' is setup correctly
        1. Should get a bunch of text and not a default error message

## Links

- Source: <https://github.com/bjyoung/Level-Up>

## License

Level Up

Copyright (c) 2022 Brandon Young

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
