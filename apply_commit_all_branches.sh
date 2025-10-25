#!/bin/bash

# 要应用的提交
COMMIT_HASH="ca8ae5e"

# 获取所有本地分支
branches=$(git for-each-ref --format='%(refname:short)' refs/heads/)

# 记录冲突分支
conflict_file="conflict_branches.txt"
> $conflict_file

# 当前分支
current_branch=$(git branch --show-current)

for branch in $branches; do
    echo "切换到分支 $branch"

    # 保存当前工作区修改
    stash_name="auto-stash-$branch-$(date +%s)"
    git stash push -u -m "$stash_name" >/dev/null 2>&1

    git checkout $branch >/dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "❌ 无法切换到 $branch"
        echo $branch >> $conflict_file
        git stash pop >/dev/null 2>&1
        continue
    fi

    # 应用提交
    git cherry-pick $COMMIT_HASH >/dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "⚠️ 冲突发生在分支 $branch，已记录到 $conflict_file"
        echo $branch >> $conflict_file
        git cherry-pick --abort >/dev/null 2>&1
    else
        echo "✔ 成功应用提交 $COMMIT_HASH 到 $branch"
    fi

    # 恢复工作区修改
    stash_list=$(git stash list)
    if [[ $stash_list == *"$stash_name"* ]]; then
        git stash pop >/dev/null 2>&1
    fi
done

# 回到原分支
git checkout $current_branch >/dev/null 2>&1
echo "操作完成，已回到分支 $current_branch"
echo "注意：以下分支存在冲突，需要手动解决："
cat $conflict_file
