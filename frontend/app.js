// ============================================================
// 备忘录系统 - 前端 JavaScript
// 纯原生 JS 实现，无需任何框架或构建工具
// ============================================================

// API 基础配置
const API_BASE = 'http://localhost:8080/api';

// 当前用户 ID（模拟登录）
const CURRENT_USER_ID = 1;

// 状态管理
let memos = [];
let categories = [];
let currentCategory = null;
let editingMemoId = null;

// ============================================================
// DOM 元素引用
// ============================================================
const elements = {
    categoryList: document.getElementById('categoryList'),
    newCategoryName: document.getElementById('newCategoryName'),
    memoTitle: document.getElementById('memoTitle'),
    memoContent: document.getElementById('memoContent'),
    memoCategory: document.getElementById('memoCategory'),
    memoDueDate: document.getElementById('memoDueDate'),
    saveBtn: document.getElementById('saveBtn'),
    cancelBtn: document.getElementById('cancelBtn'),
    formTitle: document.getElementById('formTitle'),
    statsInfo: document.getElementById('statsInfo'),
    memoList: document.getElementById('memoList'),
    errorContainer: document.getElementById('errorContainer')
};

// ============================================================
// 初始化
// ============================================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('备忘录系统初始化中...');
    loadMemos();
    loadCategories();
});

// ============================================================
// 备忘录相关操作
// ============================================================

/**
 * 加载所有备忘录
 */
async function loadMemos() {
    try {
        const response = await fetch(`${API_BASE}/memos?userId=${CURRENT_USER_ID}`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        memos = await response.json();
        renderMemoList();
        updateStats();
    } catch (error) {
        console.error('加载备忘录失败:', error);
        showError('无法加载备忘录，请检查后端服务是否启动（http://localhost:8080）');
        renderEmptyState();
    }
}

/**
 * 加载所有分类
 */
async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE}/categories?userId=${CURRENT_USER_ID}`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        categories = await response.json();
        renderCategoryList();
        updateCategorySelect();
    } catch (error) {
        console.error('加载分类失败:', error);
        // 静默失败，使用默认空分类
    }
}

/**
 * 保存备忘录（创建或更新）
 */
async function saveMemo() {
    const title = elements.memoTitle.value.trim();
    const content = elements.memoContent.value.trim();
    const categoryId = elements.memoCategory.value ? parseInt(elements.memoCategory.value) : null;
    const dueDate = elements.memoDueDate.value || null;

    if (!title) {
        alert('请输入备忘录标题');
        return;
    }

    const memoData = {
        title: title,
        content: content,
        categoryId: categoryId,
        userId: CURRENT_USER_ID,
        dueDate: dueDate
    };

    try {
        let response;
        if (editingMemoId) {
            // 更新现有备忘录
            response = await fetch(`${API_BASE}/memos/${editingMemoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(memoData)
            });
        } else {
            // 创建新备忘录
            response = await fetch(`${API_BASE}/memos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(memoData)
            });
        }

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        // 重置表单
        resetForm();
        
        // 重新加载列表
        await loadMemos();
        
        // 更新统计信息
        updateStats();
    } catch (error) {
        console.error('保存备忘录失败:', error);
        showError('保存失败，请重试');
    }
}

/**
 * 删除备忘录
 */
async function deleteMemo(id) {
    if (!confirm('确定要删除这条备忘录吗？')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/memos/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        // 重新加载列表
        await loadMemos();
        updateStats();
    } catch (error) {
        console.error('删除备忘录失败:', error);
        showError('删除失败，请重试');
    }
}

/**
 * 切换完成状态
 */
async function toggleComplete(memo) {
    try {
        const response = await fetch(`${API_BASE}/memos/${memo.id}/toggle-complete`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        // 更新本地数据
        memo.isCompleted = !memo.isCompleted;
        
        // 重新渲染
        renderMemoList();
        updateStats();
    } catch (error) {
        console.error('切换完成状态失败:', error);
        showError('操作失败，请重试');
    }
}

/**
 * 切换置顶状态
 */
async function togglePin(memo) {
    try {
        const response = await fetch(`${API_BASE}/memos/${memo.id}/toggle-pin`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        // 更新本地数据
        memo.isPinned = !memo.isPinned;
        
        // 重新渲染
        renderMemoList();
        updateStats();
    } catch (error) {
        console.error('切换置顶状态失败:', error);
        showError('操作失败，请重试');
    }
}

/**
 * 编辑备忘录
 */
function editMemo(memo) {
    editingMemoId = memo.id;
    elements.formTitle.textContent = '编辑备忘录';
    elements.saveBtn.textContent = '更新';
    elements.cancelBtn.style.display = 'inline-block';
    
    elements.memoTitle.value = memo.title || '';
    elements.memoContent.value = memo.content || '';
    elements.memoCategory.value = memo.categoryId || '';
    elements.memoDueDate.value = memo.dueDate ? formatDateTimeLocal(memo.dueDate) : '';
}

/**
 * 取消编辑
 */
function cancelEdit() {
    resetForm();
}

/**
 * 重置表单
 */
function resetForm() {
    editingMemoId = null;
    elements.formTitle.textContent = '新建备忘录';
    elements.saveBtn.textContent = '创建';
    elements.cancelBtn.style.display = 'none';
    
    elements.memoTitle.value = '';
    elements.memoContent.value = '';
    elements.memoCategory.value = '';
    elements.memoDueDate.value = '';
    
    hideError();
}

// ============================================================
// 分类相关操作
// ============================================================

/**
 * 添加新分类
 */
async function addCategory() {
    const name = elements.newCategoryName.value.trim();
    
    if (!name) {
        alert('请输入分类名称');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/categories`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: name,
                userId: CURRENT_USER_ID
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        // 清空输入框
        elements.newCategoryName.value = '';
        
        // 重新加载分类
        await loadCategories();
    } catch (error) {
        console.error('添加分类失败:', error);
        showError('添加分类失败，请重试');
    }
}

/**
 * 筛选分类
 */
async function filterCategory(categoryId) {
    currentCategory = categoryId;
    
    // 更新分类列表的选中状态
    const items = elements.categoryList.querySelectorAll('li');
    items.forEach(item => {
        item.classList.remove('active');
        if ((categoryId === null && item.textContent === '全部') ||
            (item.dataset.categoryId === String(categoryId))) {
            item.classList.add('active');
        }
    });

    // 重新加载备忘录（按分类筛选）
    await loadMemos();
}

// ============================================================
// 渲染函数
// ============================================================

/**
 * 渲染备忘录列表
 */
function renderMemoList() {
    if (memos.length === 0) {
        renderEmptyState();
        return;
    }

    // 按置顶和完成状态排序
    const sortedMemos = [...memos].sort((a, b) => {
        // 首先按置顶状态排序（置顶在前）
        if (a.isPinned !== b.isPinned) {
            return b.isPinned ? 1 : -1;
        }
        // 然后按完成状态排序（未完成在前）
        if (a.isCompleted !== b.isCompleted) {
            return a.isCompleted ? 1 : -1;
        }
        // 最后按创建时间倒序
        return new Date(b.createdAt) - new Date(a.createdAt);
    });

    // 按分类筛选
    const filteredMemos = currentCategory 
        ? sortedMemos.filter(m => m.categoryId === currentCategory)
        : sortedMemos;

    if (filteredMemos.length === 0) {
        elements.memoList.innerHTML = `
            <div class="empty-state">
                <h3>暂无备忘录</h3>
                <p>创建一条新备忘录开始吧</p>
            </div>
        `;
        return;
    }

    const html = filteredMemos.map(memo => `
        <div class="memo-item ${memo.isCompleted ? 'completed' : ''} ${memo.isPinned ? 'pinned' : ''}">
            <div class="memo-header">
                <span class="memo-title">${escapeHtml(memo.title)}</span>
                <div class="memo-actions">
                    <button onclick="togglePin(${JSON.stringify(memo).replace(/"/g, '&quot;')})" 
                            title="${memo.isPinned ? '取消置顶' : '置顶'}">
                        ${memo.isPinned ? '📌' : '📍'}
                    </button>
                    <button onclick="toggleComplete(${JSON.stringify(memo).replace(/"/g, '&quot;')})" 
                            title="${memo.isCompleted ? '标为未完成' : '标为完成'}">
                        ${memo.isCompleted ? '☑️' : '⬜'}
                    </button>
                    <button onclick="editMemo(${JSON.stringify(memo).replace(/"/g, '&quot;')})" 
                            title="编辑">
                        ✏️
                    </button>
                    <button onclick="deleteMemo(${memo.id})" 
                            title="删除">
                        🗑️
                    </button>
                </div>
            </div>
            <div class="memo-content">${escapeHtml(memo.content || '无内容')}</div>
            <div class="memo-meta">
                ${memo.categoryId ? `<span>📁 ${getCategoryName(memo.categoryId)}</span>` : ''}
                ${memo.dueDate ? `<span>⏰ ${formatDate(memo.dueDate)}</span>` : ''}
                <span>📅 ${formatDate(memo.createdAt)}</span>
            </div>
        </div>
    `).join('');

    elements.memoList.innerHTML = html;
}

/**
 * 渲染分类列表
 */
function renderCategoryList() {
    const items = categories.map(cat => `
        <li data-category-id="${cat.id}" onclick="filterCategory(${cat.id})">
            ${escapeHtml(cat.name)}
        </li>
    `).join('');

    elements.categoryList.innerHTML = `<li onclick="filterCategory(null)">全部</li>` + items;
}

/**
 * 更新分类选择下拉框
 */
function updateCategorySelect() {
    const options = categories.map(cat => 
        `<option value="${cat.id}">${escapeHtml(cat.name)}</option>`
    ).join('');
    
    elements.memoCategory.innerHTML = '<option value="">选择分类</option>' + options;
}

/**
 * 更新统计信息
 */
function updateStats() {
    const total = memos.length;
    const completed = memos.filter(m => m.isCompleted).length;
    elements.statsInfo.textContent = `总计: ${total} | 已完成: ${completed}`;
}

/**
 * 渲染空状态
 */
function renderEmptyState() {
    elements.memoList.innerHTML = `
        <div class="empty-state">
            <h3>暂无备忘录</h3>
            <p>${currentCategory ? '此分类下暂无备忘录' : '创建一条新备忘录开始吧'}</p>
        </div>
    `;
}

// ============================================================
// 工具函数
// ============================================================

/**
 * HTML 转义（防止 XSS）
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * 获取分类名称
 */
function getCategoryName(categoryId) {
    if (!categoryId) return '';
    const cat = categories.find(c => c.id === categoryId);
    return cat ? cat.name : '';
}

/**
 * 格式化日期
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

/**
 * 格式化日期时间（用于 datetime-local 输入框）
 */
function formatDateTimeLocal(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
}

/**
 * 显示错误信息
 */
function showError(message) {
    elements.errorContainer.innerHTML = `<div class="error">${escapeHtml(message)}</div>`;
}

/**
 * 隐藏错误信息
 */
function hideError() {
    elements.errorContainer.innerHTML = '';
}

// ============================================================
// 辅助函数（全局可访问）
// ============================================================
window.filterCategory = filterCategory;
window.addCategory = addCategory;
window.saveMemo = saveMemo;
window.cancelEdit = cancelEdit;
window.editMemo = editMemo;
window.deleteMemo = deleteMemo;
window.toggleComplete = toggleComplete;
window.togglePin = togglePin;