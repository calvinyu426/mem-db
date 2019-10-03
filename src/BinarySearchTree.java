import java.util.List;

public class BinarySearchTree<T extends RowData> {
    public TreeNode<T> root;
    public long size;

    public BinarySearchTree() {
        this.size = 0;
    }

    public TreeNode generateBST(List<T> dataList) {
        BinarySearchTree tree = new BinarySearchTree();
        TreeNode tmp = tree.root;
        if (dataList.isEmpty()) {
            return tmp;
        }

        tmp = insertNode(tmp, dataList.get(0));
        for (int i = 1; i < dataList.size(); i++) {
            insertNode(tmp, dataList.get(i));
        }
        return tmp;
    }

    public TreeNode<T> insertNode(TreeNode node, T row) {

        if (node == null) {
            TreeNode newNode = new TreeNode<>(row);
            ++size;

            if (null == root) {
                root = newNode;
            }
            return newNode;
        }

        if (row.compareTo(node.value) <= 0 && null != node.left) {
            return insertNode(node.left, row);
        } else {
            if (row.compareTo(node.value) <= 0) {
                node.left = new TreeNode(row);
                ++size;
                return node.left;
            } else if (row.compareTo(node.value) > 0 && null != node.right) {
                return insertNode(node.right, row);
            } else {
                node.right = new TreeNode(row);
                ++size;
                return node.right;
            }
        }
    }


    public RowData findNode(TreeNode node, ColumnValue value) {
        if (value.compareTo(node.value.key) < 0 && null != node.left) {
            return findNode(node.left, value);
        } else if (value.compareTo(node.value.key) < 0) {
            return null;
        } else if (value.compareTo(node.value.key) > 0 && null != node.right) {
            return findNode(node.right, value);
        } else if (value.compareTo(node.value.key) > 0) {
            return null;
        } else {
            return node.value;
        }
    }

    public boolean deleteNode(ColumnValue value) {
        long beforeSize = size;
        deleteNode(root, value);
        return beforeSize - 1 == size;
    }

    private TreeNode deleteNode(TreeNode node, ColumnValue value) {
        if (null == node) {
            return null;
        }

        if (value.compareTo(node.value.key) < 0) {
            node.left = deleteNode(node.left, value);
        } else if (value.compareTo(node.value.key) > 0) {
            node.right = deleteNode(node.right, value);
        } else {
            if (null != node.left && null != node.right) {
                RowData minNode = findMinValue(node.right);
                node.value = minNode;
                node.right = deleteNode(node.right, value);
            } else {
                boolean isRoot = (root == node);
                if (null == node.left) {
                    node = node.right;
                } else {
                    node = node.left;
                }
                if (isRoot) {
                    root = node;
                }
            }
            --size;
        }

        return node;
    }


    public void inOrder(TreeNode node, List<RowData> result) {
        if (node.left != null) {
            inOrder(node.left, result);
        }
        result.add(node.value);
        if (node.right != null) {
            inOrder(node.right, result);
        }
    }

    public void inOrder(TreeNode node, List<RowData> result, int count) {
        if (result.size() >= count) {
            return;
        }
        if (node.left != null) {
            inOrder(node.left, result);
        }
        result.add(node.value);
        if (node.right != null) {
            inOrder(node.right, result);
        }
    }


    private RowData findMinValue(TreeNode node) {
        if (node == null) {
            return null;
        }
        if (node.left == null) {
            return node.value;
        } else {
            return findMinValue(node.left);
        }
    }
}