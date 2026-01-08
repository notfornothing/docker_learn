# GNU Stow 学习指南

## 📋 目录

- [简介](#简介)
- [安装方法](#安装方法)
- [核心概念](#核心概念)
- [基本使用](#基本使用)
- [常用命令](#常用命令)
- [实际案例](#实际案例)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)
- [参考资源](#参考资源)

---

## 📖 简介

**GNU Stow** 是一个符号链接（symlink）管理工具，主要用于：

1. **管理 dotfiles（配置文件）**：集中管理 `.bashrc`、`.vimrc`、`.gitconfig` 等配置文件
2. **管理本地编译的软件**：将软件安装到独立目录，通过符号链接统一管理
3. **快速切换软件版本**：在不同版本之间快速切换
4. **保持多台机器配置一致**：配合 Git 实现配置的版本控制和同步

### 为什么使用 Stow？

- ✅ **组织清晰**：配置文件集中管理，结构清晰
- ✅ **易于备份**：所有配置在一个目录，方便 Git 管理
- ✅ **快速部署**：新机器上快速恢复配置
- ✅ **避免冲突**：通过符号链接，不会覆盖现有文件
- ✅ **易于卸载**：删除符号链接即可，不影响原始文件

---

## 🔧 安装方法

### macOS

```bash
# 使用 Homebrew
brew install stow

# 验证安装
stow --version
```

### Linux (Ubuntu/Debian)

```bash
sudo apt-get update
sudo apt-get install stow
```

### Linux (CentOS/RHEL)

```bash
sudo yum install stow
```

### 从源码编译

```bash
# 下载源码
wget https://ftp.gnu.org/gnu/stow/stow-latest.tar.gz
tar -xzf stow-latest.tar.gz
cd stow-*

# 编译安装
./configure
make
sudo make install
```

---

## 🎯 核心概念

### 目录结构

Stow 的工作方式基于以下目录结构：

```
~/dotfiles/              # Stow 目录（仓库目录）
├── bash/                # 包（package）
│   └── .bashrc
├── vim/
│   └── .vimrc
└── git/
    └── .gitconfig
```

### 工作原理

1. **包（Package）**：每个子目录是一个包，包含要管理的文件
2. **目标目录（Target）**：默认是 Stow 目录的父目录（如 `~`）
3. **符号链接**：Stow 在目标目录创建符号链接，指向包中的文件

### 示例流程

```bash
# 1. 创建 dotfiles 目录
mkdir -p ~/dotfiles/bash
mkdir -p ~/dotfiles/vim

# 2. 将配置文件移动到包目录
mv ~/.bashrc ~/dotfiles/bash/
mv ~/.vimrc ~/dotfiles/vim/

# 3. 使用 Stow 创建符号链接
cd ~/dotfiles
stow bash    # 在 ~ 目录创建 ~/.bashrc -> ~/dotfiles/bash/.bashrc
stow vim     # 在 ~ 目录创建 ~/.vimrc -> ~/dotfiles/vim/.vimrc
```

---

## 🚀 基本使用

### 1. 初始化 dotfiles 仓库

```bash
# 创建 dotfiles 目录
mkdir -p ~/dotfiles
cd ~/dotfiles

# 初始化 Git（可选，但推荐）
git init
```

### 2. 创建包并添加文件

```bash
# 创建包目录
mkdir -p ~/dotfiles/bash

# 将现有配置文件复制到包目录
cp ~/.bashrc ~/dotfiles/bash/

# 或者直接创建新配置文件
vim ~/dotfiles/bash/.bashrc
```

### 3. 使用 Stow 部署

```bash
cd ~/dotfiles

# 部署单个包
stow bash

# 部署所有包
stow */

# 或者明确指定
stow bash vim git
```

### 4. 撤销部署

```bash
cd ~/dotfiles

# 删除符号链接（不会删除源文件）
stow -D bash

# 删除所有包的符号链接
stow -D */
```

---

## 📝 常用命令

### 基本命令

```bash
# 部署包（创建符号链接）
stow <package-name>

# 删除包（删除符号链接）
stow -D <package-name>

# 重新部署包（先删除再创建）
stow -R <package-name>

# 查看会执行什么操作（不实际执行）
stow -n <package-name>

# 详细输出
stow -v <package-name>
```

### 高级选项

```bash
# 指定目标目录（默认是父目录）
stow -d /path/to/stow -t /path/to/target <package>

# 忽略某些文件/目录
stow --ignore='^\.git$' <package>

# 不创建父目录（如果不存在）
stow --no-folding <package>

# 允许覆盖现有文件（危险！）
stow --adopt <package>
```

### 组合使用

```bash
# 详细模式 + 模拟执行
stow -nv <package>

# 重新部署 + 详细输出
stow -Rv <package>
```

---

## 💡 实际案例

### 案例 1：管理 dotfiles

#### 目录结构

```
~/dotfiles/
├── bash/
│   ├── .bashrc
│   └── .bash_aliases
├── vim/
│   ├── .vimrc
│   └── .vim/
│       └── colors/
├── git/
│   └── .gitconfig
├── tmux/
│   └── .tmux.conf
└── zsh/
    └── .zshrc
```

#### 部署步骤

```bash
cd ~/dotfiles

# 部署所有配置
stow bash vim git tmux zsh

# 或者一次性部署所有
stow */
```

#### 结果

在 `~` 目录下会创建以下符号链接：

```
~/
├── .bashrc -> ~/dotfiles/bash/.bashrc
├── .bash_aliases -> ~/dotfiles/bash/.bash_aliases
├── .vimrc -> ~/dotfiles/vim/.vimrc
├── .vim -> ~/dotfiles/vim/.vim
├── .gitconfig -> ~/dotfiles/git/.gitconfig
├── .tmux.conf -> ~/dotfiles/tmux/.tmux.conf
└── .zshrc -> ~/dotfiles/zsh/.zshrc
```

### 案例 2：管理本地编译的软件

假设你编译安装了某个软件到 `/usr/local/stow/myapp-1.0`：

```bash
# 软件安装结构
/usr/local/stow/myapp-1.0/
├── bin/
│   └── myapp
├── lib/
│   └── libmyapp.so
└── share/
    └── myapp/

# 使用 Stow 部署
cd /usr/local/stow
stow myapp-1.0

# 结果：在 /usr/local/ 下创建符号链接
# /usr/local/bin/myapp -> /usr/local/stow/myapp-1.0/bin/myapp
# /usr/local/lib/libmyapp.so -> /usr/local/stow/myapp-1.0/lib/libmyapp.so
```

### 案例 3：快速切换软件版本

```bash
# 安装多个版本
/usr/local/stow/
├── python-3.9/
├── python-3.10/
└── python-3.11/

# 切换到 Python 3.10
cd /usr/local/stow
stow -D python-3.9    # 删除旧版本链接
stow python-3.10      # 创建新版本链接

# 或者使用重新部署
stow -R python-3.10
```

### 案例 4：配合 Git 管理配置

```bash
# 1. 初始化 Git 仓库
cd ~/dotfiles
git init
git add .
git commit -m "Initial dotfiles"

# 2. 推送到远程仓库
git remote add origin https://github.com/yourusername/dotfiles.git
git push -u origin main

# 3. 在新机器上克隆并部署
git clone https://github.com/yourusername/dotfiles.git ~/dotfiles
cd ~/dotfiles
stow */
```

---

## ✨ 最佳实践

### 1. 目录组织

```
~/dotfiles/
├── README.md           # 说明文档
├── install.sh          # 安装脚本
├── bash/
│   └── .bashrc
├── vim/
│   └── .vimrc
└── .gitignore          # Git 忽略文件
```

### 2. 创建安装脚本

创建 `~/dotfiles/install.sh`：

```bash
#!/bin/bash

# 检查 stow 是否安装
if ! command -v stow &> /dev/null; then
    echo "错误: 请先安装 stow"
    exit 1
fi

# 进入 dotfiles 目录
cd "$(dirname "$0")"

# 部署所有包
echo "正在部署配置文件..."
stow -v */

echo "完成！"
```

使用：

```bash
chmod +x ~/dotfiles/install.sh
~/dotfiles/install.sh
```

### 3. 处理已存在的配置文件

如果目标位置已有文件，Stow 默认不会覆盖。处理方式：

```bash
# 方法 1: 备份现有文件
mv ~/.bashrc ~/.bashrc.backup
stow bash

# 方法 2: 使用 --adopt（会移动现有文件到包目录）
stow --adopt bash

# 方法 3: 手动合并配置后使用 Stow
```

### 4. 使用 .gitignore

创建 `~/dotfiles/.gitignore`：

```
# 忽略敏感信息
**/secrets
**/*.key
**/*.pem

# 忽略系统特定文件
**/.DS_Store
**/Thumbs.db
```

### 5. 多机器配置管理

为不同机器创建不同的包：

```
~/dotfiles/
├── common/          # 通用配置
│   ├── .gitconfig
│   └── .vimrc
├── work/            # 工作机器配置
│   └── .bashrc
└── personal/        # 个人机器配置
    └── .bashrc
```

部署时：

```bash
# 工作机器
stow common work

# 个人机器
stow common personal
```

---

## ❓ 常见问题

### Q1: Stow 报错 "target directory does not exist"

**原因**：目标目录不存在

**解决**：

```bash
# 创建目标目录
mkdir -p ~/.config

# 或者使用 -t 指定目标目录
stow -t ~/.config config
```

### Q2: 如何处理嵌套目录？

Stow 会自动处理嵌套目录结构：

```bash
# 包结构
vim/
└── .vim/
    └── colors/
        └── mytheme.vim

# 部署后会自动创建
~/.vim/colors/mytheme.vim -> ~/dotfiles/vim/.vim/colors/mytheme.vim
```

### Q3: 如何只部署部分文件？

使用 `--ignore` 选项：

```bash
stow --ignore='^\.git$' --ignore='README\.md$' package
```

### Q4: Stow 会覆盖现有文件吗？

默认不会。如果目标位置已有文件，Stow 会报错并停止。

使用 `--adopt` 可以移动现有文件到包目录（需谨慎）。

### Q5: 如何查看当前部署了哪些包？

```bash
# 查看符号链接
ls -la ~ | grep '^l'

# 或者使用 find
find ~ -maxdepth 1 -type l -ls
```

### Q6: 如何完全卸载 Stow 管理的配置？

```bash
cd ~/dotfiles
stow -D */    # 删除所有符号链接
```

---

## 📚 参考资源

### 官方文档

- [GNU Stow 官方文档](https://www.gnu.org/software/stow/)
- [Stow 手册](https://www.gnu.org/software/stow/manual/)

### 视频教程

- [stow是令人心动的dotfiles管理器( GNU btw )](https://www.bilibili.com/video/BV1ZuBMYpEGe/)
- [从零开始的Neovim配置--第0期：使用stow管理dotfiles](https://www.bilibili.com/video/BV1no4y1s7B9/)
- [GNU Stow - 一个软链接管理器](https://www.bilibili.com/video/BV1yy4y1x7ie/)

### 文章教程

- [使用 Stow 管理多台机器配置](https://lctt.x-cmd.com/202001/20200112%20Use%20Stow%20for%20configuration%20management%20of%20multiple%20machines/)
- [Using GNU Stow to manage your dotfiles](https://alexpearce.me/2016/02/managing-dotfiles-with-stow/)

### GitHub 示例

- 搜索 "dotfiles stow" 可以找到很多实际使用案例

---

## 🎓 学习路径建议

1. **第一步**：安装 Stow，熟悉基本命令
2. **第二步**：用 Stow 管理一个简单的配置文件（如 `.bashrc`）
3. **第三步**：扩展管理更多配置文件
4. **第四步**：配合 Git 实现配置的版本控制
5. **第五步**：在多台机器上同步配置

---

**最后更新**: 2025-01-27

**祝你学习愉快！** 🎉

